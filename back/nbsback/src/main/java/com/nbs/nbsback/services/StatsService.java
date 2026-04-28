package com.nbs.nbsback.services;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nbs.nbsback.models.Activity;
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

        

        public Stat getStatById(Long id) {
                Stat stat = statRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Stat not found with ID: " + id));
                return hydrateStatActivities(stat);
        }

        public List<Stat> getAllStats(LocalDate date, String type) {
                List<Stat> stats;

                if (type != null && date != null) {
                        stats = statRepository.findAll().stream()
                                        .filter(stat -> stat.getType().name().equalsIgnoreCase(type))
                                        .filter(stat -> date.isAfter(stat.getStartDate().toLocalDate()) && date.isBefore(stat.getEndDate().toLocalDate()))
                                        .collect(Collectors.toList());
                } else if (type != null) {
                        stats = statRepository.findAll().stream()
                                        .filter(stat -> stat.getType().name().equalsIgnoreCase(type))
                                        .collect(Collectors.toList());
                } else if (date != null) {
                        stats = statRepository.findStatsByDateInRange(date.atStartOfDay());
                } else {
                        stats = statRepository.findAll();
                }

                return hydrateStatsActivities(stats);
        }

        public List<Stat> getStatsByActivityId(Long activityId) {
                return hydrateStatsActivities(statRepository.findAll()).stream()
                                .filter(stat -> stat.getActivityIds().contains(activityId))
                                .collect(Collectors.toList());
        }

        public List<Stat> getStatsByTypeAndDate(String type, LocalDate date) {
                List<Stat> stats;

                if (type != null && date != null) {
                        stats = statRepository.findAll().stream()
                                        .filter(stat -> stat.getType().name().equalsIgnoreCase(type))
                                        .filter(stat -> date.isAfter(stat.getStartDate().toLocalDate()) && date.isBefore(stat.getEndDate().toLocalDate()))
                                        .collect(Collectors.toList());
                } else if (type != null) {
                        stats = statRepository.findAll().stream()
                                        .filter(stat -> stat.getType().name().equalsIgnoreCase(type))
                                        .collect(Collectors.toList());
                } else if (date != null) {
                        stats = statRepository.findStatsByDateInRange(date.atStartOfDay());
                } else {
                        stats = statRepository.findAll();
                }

                return hydrateStatsActivities(stats);
        }

        public void syncStatsForActivity(Long objectId) {
                logger.info("Syncing stats for activity ID: {}", objectId);

                Activity newActivity = activityRepository.findById(objectId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Activity not found with ID: " + objectId));

                logger.info("Found activity: {} - {} - {}", newActivity.getId(), newActivity.getName(),
                                newActivity.getStartDate());

                Long athleteId = newActivity.getAthleteId();
                LocalDateTime activityDate = newActivity.getStartDateLocal();

                // Ensure stats exist for the activity
                ensureStatsForActivity(athleteId, activityDate);

                // Sync activity with existing stats
                List<Stat> allStats = statRepository.findStatsByDateInRangeAndAthleteId(activityDate, athleteId);

                allStats.forEach(stat -> {
                        logger.info("Stat: {} - {} - {} to {}", stat.getId(), stat.getType(), stat.getStartDate(),
                                        stat.getEndDate());

                        stat.addActivityId(objectId);
                        List<Activity> updatedActivities = activityRepository.findAllById(stat.getActivityIds());
                        stat.setActivities(updatedActivities);
                        stat.calculateStats(updatedActivities);
                        statRepository.save(stat);

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
                if (!statRepository.existsByTypeAndAthleteIdAndStartDateAndEndDate(type, athleteId, startDate,
                                endDate)) {
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