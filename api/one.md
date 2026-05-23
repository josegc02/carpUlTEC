
# Google Maps API - Collection Postman


---

## Configuración previa

| Variable | Valor |
|----------|-------|
| Base URL | `https://maps.googleapis.com/maps/api` |
| API Key  | `AIzaSyBMKo7EbRUFhQwtCO7VpDdn_PMLJlbRFrE` |

---

## Endpoints

### 1. Geocoding API
Convierte una dirección de texto a coordenadas lat/lng.

**Método:** `GET`  
**URL:**
```
https://maps.googleapis.com/maps/api/geocode/json
```

**Params:**

| Key | Value | Descripción |
|-----|-------|-------------|
| `address` | `Lima, Peru` | Dirección a convertir |
| `key` | `AIzaSyBMKo7Eb...` | API Key |

**Respuesta exitosa:**
```json
{
    "results": [
        {
            "geometry": {
                "location": {
                    "lat": -12.0463731,
                    "lng": -77.042754
                }
            },
            "formatted_address": "Lima, Peru"
        }
    ],
    "status": "OK"
}
```

---

### 2. Directions API
Calcula la ruta entre conductor y pasajero.

**Método:** `GET`  
**URL:**
```
https://maps.googleapis.com/maps/api/directions/json
```

**Params:**

| Key | Value | Descripción |
|-----|-------|-------------|
| `origin` | `-12.0464,-77.0428` | Coordenadas de origen |
| `destination` | `-12.1219,-77.0299` | Coordenadas de destino |
| `mode` | `driving` | Modo de transporte |
| `key` | `AIzaSyBMKo7Eb...` | API Key |

**Respuesta exitosa:**
```json
{
    "routes": [
        {
            "summary": "Av. Javier Prado",
            "legs": [
                {
                    "distance": { "text": "12.3 km", "value": 12300 },
                    "duration": { "text": "25 mins", "value": 1500 }
                }
            ]
        }
    ],
    "status": "OK"
}
```

---

### 3. Distance Matrix API
Calcula distancia y tiempo estimado para la tarifa.

**Método:** `GET`  
**URL:**
```
https://maps.googleapis.com/maps/api/distancematrix/json
```
**Params:**

| Key | Value | Descripción |
|-----|-------|-------------|
| `origins` | `-12.0464,-77.0428` | Punto de origen |
| `destinations` | `-12.1219,-77.0299` | Punto de destino |
| `mode` | `driving` | Modo de transporte |
| `units` | `metric` | Sistema métrico |
| `key` | `AIzaSyBMKo7Eb...` | API Key |


**Respuesta exitosa:**
```json
{
    "rows": [
        {
            "elements": [
                {
                    "distance": { "text": "12.3 km", "value": 12300 },
                    "duration": { "text": "25 mins", "value": 1500 },
                    "status": "OK"
                }
            ]
        }
    ],
    "status": "OK"
}
```

---

## Errores comunes

| Status | Causa | Solución |
|--------|-------|----------|
| `REQUEST_DENIED` | API Key incorrecta o IP bloqueada | Verificar key o quitar restricción IP |
| `INVALID_REQUEST` | Faltan parámetros | Verificar que todos los params estén completos |
| `OVER_DAILY_LIMIT` | Billing no activado | Activar billing en Google Cloud |
| `OVER_QUERY_LIMIT` | Límite de cuota superado | Esperar o aumentar cuota |

---

## Archivos

| Archivo | Descripción |
|---------|-------------|
| `google-maps-api.postman_collection.json` | Collection lista para importar en Postman |
| `README.md` | Esta documentación |

---

## Proyecto Google Cloud

- **Proyecto:** `my-sales-app-485901`
- **APIs habilitadas:** Geocoding API, Directions API, Distance Matrix API
- **Restricción de key:** Direcciones IP (backend Spring Boot)


