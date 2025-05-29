## Dokumentacja na potrzeby oddawania projektu

### Działanie projektu

Projekt został stworzony używając
- Springboot security z 2 typami ról ADMIN i USER

- Wykorzystywany jest JWT do autoryzacji

- Baza danych to PostgreSQL

- Wykorzystano Swagger do dokumentacji API z wsparciem tokenów JWT
  - ![img_7.png](docs/img_7.png)

- Zostały dodane testy jednostkowe i integracyjne (z naciskiem na integracyjne) z użyciem H2
  - ![img_6.png](docs/img_6.png)

- Korzysta z flyway do migracji bazy danych, migracje w src/main/resources/db/migration
  - ![img_5.png](docs/img_5.png)
- Korzysta z docker-compose.yml

- Polimorfizm został użyty w typach tasków zwracanych z api i klasę TaskInfo
  - ![img.png](docs/img.png)
  - ![img_1.png](docs/img_1.png)

- Wykorzystano wzorzec projektowy Builder do tworzenia tasków
  - ![img_2.png](docs/img_2.png)
  - ![img_3.png](docs/img_3.png)

- Korzysta z klas DTO do komunikacji z API i serwisami
  - ![img_4.png](docs/img_4.png)

- .env i .env.local pliki do konfiguracji środowiska
- Tworzenie użytkowników o różnych rolach jest zaimplementowane używając PASSKEY w .env
  - ![img_8.png](docs/img_8.png)
  - ![img_9.png](docs/img_9.png)

### ERD
![ERD Diagram.png](docs/ERD%20Diagram.png)

### Coverage
![Test Coverage.png](docs/Test%20Coverage.png)