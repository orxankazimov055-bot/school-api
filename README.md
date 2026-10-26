# School API — Spring Boot + Spring Data JPA

## Layihə haqqında

School API — Java 25, Spring Boot 4 və PostgreSQL istifadə edilərək hazırlanmış REST API layihəsidir.

Layihədə JPA entity əlaqələri, Spring Data JPA repository-ləri, DTO, MapStruct mapper, service və controller qatları istifadə olunur.

## İstifadə olunan texnologiyalar

- Java 25
- Spring Boot 4
- Spring Data JPA / Hibernate
- PostgreSQL 16
- Docker Compose
- Gradle
- Lombok
- MapStruct
- Postman

## Layihənin işə salınması

1. Java 25 və Docker Desktop quraşdırın.
2. Terminalda layihənin əsas qovluğuna keçin.
3. PostgreSQL konteynerini başladın:

```bash
docker compose up -d
```

4. Spring Boot tətbiqini başladın:

```bash
./gradlew bootRun
```

Windows üçün:

```bat
gradlew.bat bootRun
```

5. API-nin əsas ünvanı:

`http://localhost:8080/test-demo`

Verilənlər bazası: `school_db`

## JPA əlaqələri

| Əlaqə | Entity-lər |
|---|---|
| OneToOne | User — Passport |
| OneToMany / ManyToOne | Department — Employee |
| ManyToMany | Student — Course |
| ManyToMany (ara entity) | Author — AuthorBook — Book |

## REST API endpoint-ləri

Bütün ünvanlar `http://localhost:8080/test-demo` ünvanından başlayır.

### User və Passport

| Metod | Endpoint | İzah |
|---|---|---|
| GET | `/api/users` | İstifadəçiləri göstərmək |
| GET | `/api/users/{id}` | İstifadəçini ID ilə tapmaq |
| POST | `/api/users` | İstifadəçi yaratmaq |
| POST | `/api/users/{id}/passport` | Pasport əlavə etmək |
| DELETE | `/api/users/{id}/passport` | Pasport əlaqəsini silmək |

### Department və Employee

| Metod | Endpoint | İzah |
|---|---|---|
| POST | `/api/departments` | Departament yaratmaq |
| GET | `/api/departments/{id}` | Departamenti göstərmək |
| POST | `/api/employees/department/{id}` | Departamentə işçi əlavə etmək |
| DELETE | `/api/employees/{id}` | İşçini silmək |

### Student və Course

| Metod | Endpoint | İzah |
|---|---|---|
| POST | `/api/students` | Tələbə yaratmaq |
| GET | `/api/students/{id}` | Tələbəni göstərmək |
| POST | `/api/courses` | Kurs yaratmaq |
| POST | `/api/students/{studentId}/courses/{courseId}` | Tələbəni kursa əlavə etmək |

### Author, Book və AuthorBook

| Metod | Endpoint | İzah |
|---|---|---|
| POST | `/api/authors` | Müəllif yaratmaq |
| GET | `/api/authors` | Müəllifləri göstərmək |
| POST | `/api/books` | Kitab yaratmaq |
| POST | `/api/author-books/authors/{authorId}/books/{bookId}` | Müəlliflə kitab arasında əlaqə yaratmaq |
| GET | `/api/author-books` | Müəllif-kitab əlaqələrini göstərmək |
| DELETE | `/api/author-books/authors/{authorId}/books/{bookId}` | Müəllif-kitab əlaqəsini silmək |

Qeyd: Yuxarıdakı siyahı indiyədək yoxladığımız endpoint-ləri əhatə edir. GitHub-a yükləməzdən əvvəl controller-lərdəki bütün digər endpoint-lər də siyahıya əlavə edilməlidir.

## JPA davranışları

- `cascade` əlaqəli entity-lərdə hansı əməliyyatların ötürüləcəyini müəyyən edir.
- `orphanRemoval` əlaqədən çıxarılan uşaq entity-nin silinməsini təmin edir.
- `mappedBy` iki istiqamətli əlaqədə sahib olmayan tərəfi göstərir.
- `student_course` tələbə-kurs əlaqələrini saxlayır.
- `author_book` müəllif-kitab əlaqəsini ayrıca entity vasitəsilə saxlayır.

## Postman

API endpoint-lərinin testləri Postman vasitəsilə aparılır. Postman collection JSON faylı layihə ilə birlikdə repoya əlavə olunmalıdır.
