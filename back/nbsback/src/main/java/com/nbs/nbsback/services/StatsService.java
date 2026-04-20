package com.nbs.nbsback.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nbs.nbsback.models.Activity;
import com.nbs.nbsback.models.Athlete;
import com.nbs.nbsback.models.Stat;
import com.nbs.nbsback.models.StatType;
import com.nbs.nbsback.repositories.ActivityRepository;
import com.nbs.nbsback.repositories.StatRepository;

@Service
public class StatsService {

        @Autowired
        private ActivityRepository activityRepository;

        @Autowired
        private StatRepository statRepository;

        private static final Logger logger = LoggerFactory.getLogger(StatsService.class);

        public Boolean calculateAllStats(int year, Athlete athlete) {

                //Eliminar todas las stats anteriores del atleta
                statRepository.deleteByAthleteId(athlete.getId());

                // Use January 1st as the start of the year
                LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0, 0);
                LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);

                List<Activity> allActivities = activityRepository.findByStartDateBetweenAndAthleteIdAndType(startOfYear,
                                endOfYear, athlete.getId(), "Run");

                calculateYearlyStats(athlete.getId(), startOfYear, endOfYear, allActivities);
                calculateMonthlyStats(athlete.getId(), startOfYear, endOfYear, allActivities);
                calculateWeeklyStats(athlete.getId(), startOfYear, endOfYear, allActivities);

                return true;
        }

        private void calculateYearlyStats(Long athleteId, LocalDateTime startOfYear, LocalDateTime endOfYear,
                        List<Activity> allActivities) {

                // Calculate yearly stats
                Stat yearlyStat = new Stat(StatType.ANNUAL, allActivities, startOfYear, endOfYear, athleteId);

                statRepository.save(yearlyStat);
        }

        private void calculateWeeklyStats(Long athleteId, LocalDateTime startOfYear, LocalDateTime endOfYear,
                        List<Activity> allActivities) {
                // Adjust startOfYear to the Monday before January 1st
                while (startOfYear.getDayOfWeek() != DayOfWeek.MONDAY) {
                        startOfYear = startOfYear.minusDays(1);
                }

                // Calculate weekly stats
                Stream.iterate(startOfYear, periodStart -> periodStart.isBefore(endOfYear),
                                periodStart -> periodStart.plusWeeks(1))
                                .forEach(periodStart -> {
                                        LocalDateTime periodEnd = periodStart.plusDays(6).isBefore(endOfYear)
                                                        ? periodStart.plusDays(6).withHour(23).withMinute(59)
                                                                        .withSecond(59)
                                                        : endOfYear;

                                        List<Activity> periodActivities = allActivities.stream()
                                                        .filter(activity -> !activity.getStartDate()
                                                                        .isBefore(periodStart)
                                                                        && !activity.getStartDate().isAfter(periodEnd))
                                                        .toList();

                                        
                                                Stat weeklyStat = new Stat(StatType.WEEKLY, periodActivities,
                                                                periodStart,
                                                                periodEnd, athleteId);
                                                statRepository.save(weeklyStat);
                                        
                                });
        }

        private void calculateMonthlyStats(Long athleteId, LocalDateTime startOfYear, LocalDateTime endOfYear,
                        List<Activity> allActivities) {
                // Calculate monthly stats for all 12 months of the year
                Stream.iterate(startOfYear, periodStart -> periodStart.isBefore(endOfYear),
                                periodStart -> periodStart.plusMonths(1))
                                .forEach(periodStart -> {

                                        LocalDateTime periodEnd = periodStart.plusMonths(1).minusSeconds(1)
                                                        .isBefore(endOfYear)
                                                                        ? periodStart.plusMonths(1).minusSeconds(1)
                                                                        : endOfYear;

                                        List<Activity> periodActivities = allActivities.stream()
                                                        .filter(activity -> !activity.getStartDate()
                                                                        .isBefore(periodStart)
                                                                        && !activity.getStartDate().isAfter(periodEnd))
                                                        .toList();

                                        
                                                Stat monthlyStat = new Stat(StatType.MONTHLY, periodActivities,
                                                                periodStart, periodEnd, athleteId);
                                                statRepository.save(monthlyStat);
                                        

                                });
        }

        public Stat getStatById(Long id) {
                Stat stat = statRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Stat not found with ID: " + id));
                return hydrateStatActivities(stat);
        }

        public List<Stat> getAllStats(LocalDate date) {
                if (date != null) {
                        return hydrateStatsActivities(statRepository.findStatsByDateInRange(date.atStartOfDay()));
                }

                return hydrateStatsActivities(statRepository.findAll());
        }

        public List<Stat> getStatsByActivityId(Long activityId) {
                return hydrateStatsActivities(statRepository.findAll()).stream()
                                .filter(stat -> stat.getActivityIds().contains(activityId))
                                .collect(Collectors.toList());
        }

        public void syncStatsForActivity(Long objectId) {
                logger.info("Syncing stats for activity ID: {}", objectId);

                Activity newActivity = activityRepository.findById(objectId)
                        .orElseThrow(() -> new IllegalArgumentException(
                                "Activity not found with ID: " + objectId));

                logger.info("Found activity: {} - {} - {}", newActivity.getId(), newActivity.getName(), newActivity.getStartDate());

                Long athleteId = newActivity.getAthleteId();
                LocalDateTime activityDate = newActivity.getStartDateLocal();

                // Ensure stats exist for the activity
                ensureStatsForActivity(athleteId, activityDate);

                // Sync activity with existing stats
                List<Stat> allStats = statRepository.findStatsByDateInRangeAndAthleteId(activityDate, athleteId);
                allStats.forEach(stat -> {
                        logger.info("Stat: {} - {} - {} to {}", stat.getId(), stat.getType(), stat.getStartDate(), stat.getEndDate());
                        if (!stat.getActivityIds().contains(objectId)) {
                                stat.addActivityId(objectId);
                                List<Activity> updatedActivities = activityRepository.findAllById(stat.getActivityIds());
                                stat.setActivities(updatedActivities);
                                stat.calculateStats(updatedActivities);
                                statRepository.save(stat);
                        }
                });
        }

        private void ensureStatsForActivity(Long athleteId, LocalDateTime activityDate) {
                LocalDateTime startOfYear = activityDate.withDayOfYear(1).toLocalDate().atStartOfDay();
                LocalDateTime endOfYear = startOfYear.plusYears(1).minusSeconds(1);
                ensureStatExists(StatType.ANNUAL, athleteId, startOfYear, endOfYear);

                LocalDateTime startOfMonth = activityDate.withDayOfMonth(1).toLocalDate().atStartOfDay();
                LocalDateTime endOfMonth = startOfMonth.plusMonths(1).minusSeconds(1);
                ensureStatExists(StatType.MONTHLY, athleteId, startOfMonth, endOfMonth);

                LocalDateTime startOfWeek = activityDate.with(DayOfWeek.MONDAY).toLocalDate().atStartOfDay();
                LocalDateTime endOfWeek = startOfWeek.plusDays(6).withHour(23).withMinute(59).withSecond(59);
                ensureStatExists(StatType.WEEKLY, athleteId, startOfWeek, endOfWeek);
        }

        private void ensureStatExists(StatType type, Long athleteId, LocalDateTime startDate, LocalDateTime endDate) {
                if (!statRepository.existsByTypeAndAthleteIdAndStartDateAndEndDate(type, athleteId, startDate, endDate)) {
                        Stat stat = new Stat(type, new ArrayList<>(), startDate, endDate, athleteId);
                        statRepository.save(stat);
                }
        }

        private List<Stat> hydrateStatsActivities(List<Stat> stats) {
                return stats.stream().map(this::hydrateStatActivities).collect(Collectors.toList());
        }

        private Stat hydrateStatActivities(Stat stat) {
                List<Long> activityIds = stat.getActivityIds();
                if (activityIds.isEmpty()) {
                        stat.setActivities(new ArrayList<>());
                        return stat;
                }

                stat.setActivities(activityRepository.findAllById(activityIds));
                return stat;
        }

}