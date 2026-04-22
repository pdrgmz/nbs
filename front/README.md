# NBS Front

Base de frontend en Vue 3 + Vite para conectarse con la API de NBS expuesta en `https://nbsb.pdrgmz.uk`.

## Incluye

- Router con vistas iniciales para resumen, actividades y referencia de API.
- Cliente HTTP centralizado con `axios`.
- Configuracion por entorno con `VITE_API_BASE_URL` y `VITE_SWAGGER_URL`.
- Soporte para guardar un token Bearer en `localStorage` y adjuntarlo automaticamente a las peticiones.
- Pantalla inicial que prueba `GET /health`.

## Estructura

- `src/api`: cliente Axios y modulos por recurso.
- `src/config`: variables de entorno normalizadas.
- `src/composables`: utilidades reutilizables para estado asincrono.
- `src/layouts`: shell principal de la aplicacion.
- `src/router`: definicion de rutas.
- `src/views`: pantallas base de la app.

## Entorno

Duplica `.env.example` si necesitas cambiar la URL del backend.

```bash
VITE_API_BASE_URL=https://nbsb.pdrgmz.uk
VITE_SWAGGER_URL=https://nbsb.pdrgmz.uk/swagger-ui/index.html#/
```

## Scripts

```bash
npm run dev
npm run build
npm run preview
```

## Siguiente capa recomendada

- Añadir autenticacion real con login y refresh token si el backend lo expone.
- Separar modulos de dominio adicionales: `athletes`, `streams`, `stats`.
- Introducir gestion de estado compartido cuando aparezcan flujos entre vistas.
