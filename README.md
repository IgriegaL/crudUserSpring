# crudUserSpring
El siguiente es un proyecto con las siguientes Tecnologías:
- Java 17
- Spring
- Postgres

EL proyecto es un crud de usuarios con seguridad para ingresar solo si el usuario está registrado en la base de datos.

- Si el usuario se encuentra registrado, puede ingresar a /login, entregando por Body el user y contraseña, 
lo que devolverá un Bearer Token por 10 días de validéz. en el siguiente Enpoint: 
  LocalHost:Puerto/users/login
  
- El desarrollo permite revisar todos los usuarios registrados en el enpoint: 
  LocalHost:Puerto/users/
  
- El desarrollo permite registrar un nuevo usuario en el siguiente endpoint: 
  LocalHost:Puerto/users/
  Entregando por Body la siguiente estructura de ejemplo:
  {
    "name": "jose jose",
    "email": "jose@example.com",
    "password": "123456",
    "active": true,
    "phones": [
        {
            "number": "1234567890",
            "citycode": "1",
            "countrycode": "57"
        }
    ]
  }
  
- El desarrollo permite editar un usuario ya registrado en el siguiente endpoint: 
  LocalHost:Puerto/users/{id}
  {
    "id": 24,
    "name": "jose jose",
    "email": "jose@example.com",
    "password": "123456",
    "active": true,
    "phones": [
        {
            "number": "1234567890",
            "citycode": "1",
            "countrycode": "57"
        }
    ]
  }
  
- El desarrollo permite eliminar un usuario ya registrado en el siguiente endpoint: 
  LocalHost:Puerto/users/{id}

  

 
