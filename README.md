# School API

Java 25, Spring Boot 4, PostgreSQL 16, Gradle, Liquibase və Hibernate JCache/Ehcache ilə REST API.
JPA əlaqələri: User–Passport (OneToOne), Department–Employee (OneToMany), Student–Course (ManyToMany), Author–AuthorBook–Book (ara entity).

## 1. Başlamaq

Java 25, Docker Desktop və Git lazımdır. Layihənin əsas qovluğunda:

```bash
docker compose up -d
./gradlew bootRun
```

Windows PowerShell-də `./gradlew` əvəzinə `.\gradlew.bat` yazın.
API ünvanı: `http://localhost:8080/test-demo`.
DB: `school_liquibase_db`, port `5432`, yerli demo istifadəçisi/parolu `postgres`.
Compose və application.yaml eyni DB adından istifadə edir.
Mövcud Docker volume varsa, POSTGRES_DB dəyişməsi köhnə volume daxilində yeni baza yaratmır.
Mövcud məlumatları silməyin; bazanın adını yoxlayın və lazım olduqda `SPRING_DATASOURCE_URL` ilə düzgün bazanı seçin.

Hibernate `ddl-auto: validate` istifadə edir. Sxem dəyişikliklərini Liquibase idarə edir.
`dev` context-i demo məlumatlarını əlavə edir. Testlər mövcud PostgreSQL bazasına qoşulur və tətbiq başlananda Liquibase işləyir; onları ayrıca inkişaf/test bazasında işə salın.

## 2. Liquibase əmrləri

Gradle tapşırıqları tətbiqlə eyni Liquibase və PostgreSQL JDBC versiyalarından istifadə edir; ayrıca CLI quraşdırmaq lazım deyil.
Layihənin əsas qovluğunda PowerShell:

```powershell
$env:LIQUIBASE_COMMAND_URL = 'jdbc:postgresql://localhost:5432/school_liquibase_db'
$env:LIQUIBASE_COMMAND_USERNAME = 'postgres'
$env:LIQUIBASE_COMMAND_PASSWORD = 'postgres'
.\gradlew.bat liquibaseValidate
.\gradlew.bat liquibaseStatus
.\gradlew.bat liquibaseUpdate
```

Linux/macOS/Git Bash:

```bash
export LIQUIBASE_COMMAND_URL=jdbc:postgresql://localhost:5432/school_liquibase_db
export LIQUIBASE_COMMAND_USERNAME=postgres
export LIQUIBASE_COMMAND_PASSWORD=postgres
./gradlew liquibaseValidate liquibaseStatus
./gradlew liquibaseUpdate
```

`update` gözləyən changeSet-ləri `dev` context-i ilə tətbiq edir. `status` gözləyən changeSet-ləri göstərir; `validate` changelog yoxlamasıdır.

Rollback SQL-ni əvvəlcə yoxlamaq və sonra yalnız ehtiyat nüsxəsi olan test bazasında icra etmək üçün:

```powershell
.\gradlew.bat liquibaseRollbackSql -ProllbackTag=v1.0
.\gradlew.bat liquibaseRollback -ProllbackTag=v1.0
.\gradlew.bat liquibaseStatus
```

**Mövcud məhdudiyyət:** `008-1` üçün açıq rollback yoxdur; `modifyDataType` və xam SQL avtomatik tam geri çevrilə bilmir. `100-2`–`100-6` xam seed SQL-lərinin də rollback-i yoxdur. Buna görə mövcud tam changelog-dan `v1.0` tag-inə rollback uğurlu hesab edilmir və bu yoxlamada icra edilməyib. Əmrlər bu məhdudiyyəti gizlətmir. Köhnə tətbiq olunmuş changeSet-lərə təsdiqsiz rollback əlavə edilməyib; tamamlanması üçün tətbiq tarixçəsi və müəllimin gözlədiyi rollback ssenarisi dəqiqləşməlidir.

Changelog: `src/main/resources/db/changelog/db.changelog-master.yaml`.
Liquibase əmrlərinin rəsmi izahı: [rollback](https://docs.liquibase.com/community/reference-guide-5-0/init-update-and-rollback-commands/rollback).

## 3. Cache endpoint-ləri və yoxlama

| Metod | Endpoint | Məqsəd |
|---|---|---|
| GET | `/api/departments/1` | Department L2 cache |
| GET | `/api/authors/by-country?country=United%20Kingdom` | Author query cache |

Postman-da eyni GET sorğusunu iki dəfə göndərin. Seed məlumatlarında United Kingdom üçün George Orwell və Jane Austen var. Dəyişdirilmiş bazada nəticə fərqli ola bilər.
Eyni JSON cavabı cache hit sübutu deyil. Testlər Hibernate `Statistics` sayğaclarını yoxlayır:

```powershell
.\gradlew.bat test
```

27.10.2026 tarixində ayrıca təqdimetmə nüsxəsində iki test keçdi: `DepartmentCacheTest` (L2 hit 2), `AuthorQueryCacheTest` (query hit 1, miss 1). Bu nəticə bütün transaction və isolation ssenarilərinin avtomatik yoxlanması demək deyil.

## 4. Transactions

| Metod | Endpoint | Davranış |
|---|---|---|
| POST | `/api/employees/{id}/transfer` | REQUIRED ilə şöbə və maaş bir transaction-da dəyişir |
| POST | `/api/students/{studentId}/enroll/{courseId}` | REQUIRED daxilindən MANDATORY çağırılır |
| POST | `/api/tx-demo/reserve-seat/{courseId}` | Transaction olmadıqda 409 gözlənilir |

Transfer body:

```json
{"departmentId":2,"raisePercent":5}
```

`SalaryService` 10000 AZN-dən yuxarı maaşı rədd edir; runtime exception transferdə həm şöbə, həm maaş dəyişikliyini rollback etdirir.
`TransferAuditService` transaction xaricindən transferi çağırır, nəticəni ayrıca `AuditLogService` REQUIRES_NEW transaction-ı ilə yazır. FAILED audit qeydi əsas transferin rollback-indən sonra saxlanır. Audit yazılması özü uğursuz olarsa bu zəmanət verilmir.
`reserveSeat` hazırda propagation nümunəsidir: yalnız log yazır, capacity azaltmır və yer sayını yoxlamır.
Postman-dakı ID-lər nümunədir. Xüsusilə employee `11` təmiz seed bazasında yoxdur; `/api/employees` ilə mövcud ID seçin. Yazma sorğuları məlumatı dəyişir.

## 5. Isolation və scripts/isolation-demo.sh

| Metod | Endpoint |
|---|---|
| GET | `/api/isolation/read-committed` |
| GET | `/api/isolation/serializable` |

Endpoint-lər iki count arasında 10 saniyə gözləyir, nəticədə yalnız ikinci count-u qaytarır; hər iki oxuma tətbiq logunda görünür. 200 OK özü isolation sübutu deyil.

İki paralel PostgreSQL session-u ilə ayrıca SQL nümayişi:

```bash
# Layihənin əsas qovluğunda, Docker işləyərkən:
bash scripts/isolation-demo.sh
# Başqa baza adı üçün:
DB_NAME=school_liquibase_db bash scripts/isolation-demo.sh
```

Windows-da **Git Bash** açın və eyni əmri yazın. Skript Docker Compose və konteynerdəki `psql` istifadə edir; kompüterdə ayrıca psql lazım deyil. Tətbiqin işləməsi tələb olunmur.
Skript yalnız özü yaratdığı unikal demo cədvəlini dəyişir və sonda silir. Session A ilk count-u oxuyub gözləyir, session B bir sətri commit edir, sonra A yenidən oxuyur. Bu, API endpoint testi deyil; SQL isolation nümayişidir.

Gözlənilən nəticə:

```text
READ COMMITTED: counts=[0 1], expected=[0 1]
REPEATABLE READ: counts=[0 0], expected=[0 0]
SERIALIZABLE: counts=[0 0], expected=[0 0]
```

Skript gözlənilən count-ları yoxlayır və uyğunsuzluqda xəta ilə dayanır. Sintaksisi yoxlanıb; canlı Docker icrası bu təhvil yoxlamasında aparılmayıb.

## 6. Mövzular üzrə sual və cavablar

Tam müəllim sualları bu nüsxə hazırlanarkən təqdim olunmayıb. Aşağıdakılar görünən tələblərə cavablardır; orijinal suallarla maddə-maddə tutuşdurmaq hələ lazımdır.

**Liquibase nə üçündür?** Sxem və məlumat miqrasiyalarını sıralı changeSet-lərlə versiyalaşdırır. Hibernate validate mövcud sxemi entity-lərlə yoxlayır.

**Tətbiq olunmuş changeSet niyə dəyişdirilmir?** Liquibase identity-ni id, author və fayl yolu ilə izləyir, checksum ilə məzmun dəyişikliyini yoxlayır. Yeni tələb üçün yeni changeSet yazılır. `clearCheckSums`, `validCheckSum` və MARK_RAN ilə problemi sübutsuz gizlətmək olmaz.

**Git tarixçəsi tətbiq tarixçəsini sübut edirmi?** Xeyr. Yerli tarixçədə changelog-lar yalnız `9f5e205` commit-ində əlavə olunub, sonrakı commit-də dəyişdirilmə görünmür. Commit-dən əvvəl tətbiq olunub sonra dəyişdirilib-dəyişdirilmədiyi məlum deyil. Test başlanğıcında Liquibase “Database is up to date” və checksum xətası olmadan işləyib. Bu cari vəziyyətin yoxlamadan keçdiyini göstərir; keçmişdə checksum sıfırlanması və ya MARK_RAN olub-olmadığını sübut etmir.

**Daha güclü tarixçə sübutu necə alınır?** İcazəli baza konsolunda aşağıdakı read-only SQL nəticəsini Git commit vaxtları və köhnə changelog nüsxələri ilə müqayisə edin:

```sql
SELECT id, author, filename, dateexecuted, orderexecuted, exectype, md5sum
FROM databasechangelog ORDER BY orderexecuted;
```

DATEEXECUTED təkbaşına məzmunu sübut etmir. Bu yoxlamada həmin sətirlər ayrıca oxunmayıb.

**L1 və L2 cache fərqi nədir?** L1 bir persistence context üçündür; L2 müxtəlif session-lar arasında entity məlumatını paylaşır. Entity L2 cache üçün ayrıca işarələnir və provider qurulur.

**Query cache nə saxlayır?** Cacheable sorğunun nəticəsini saxlayır; entity query-lərində nəticənin materiallaşdırılması Hibernate versiyası/layout və entity cache-dən asılıdır. Burada repository query hint-i ilə aktivləşdirilir. Country parametrinin dəyişməsi başqa cache açarı yaradır. Boş nəticə də cache edilə bilər.

**Cache işlədiyini necə sübut edirik?** Ayrı session-lar və Hibernate Statistics hit/miss sayğacları ilə. Təkcə sürət və ya eyni JSON kifayət deyil.

**REQUIRED, REQUIRES_NEW, MANDATORY fərqi nədir?** REQUIRED mövcud transaction-a qoşulur və ya yaradır. REQUIRES_NEW müstəqil transaction yaradır, varsa xaricdəki transaction-ı dayandırır. MANDATORY yalnız mövcud transaction daxilində işləyir. Metodlar ayrı Spring bean-ləri vasitəsilə çağırılır ki, transaction proxy-si işləsin.

**Rollback nə vaxt olur?** Spring-in standart davranışında RuntimeException və Error rollback yaradır; checked exception üçün ayrıca rollbackFor lazım ola bilər. Exception-u tutub udmaq rollback davranışını dəyişə bilər.

**READ COMMITTED, REPEATABLE READ, SERIALIZABLE fərqi nədir?** PostgreSQL READ COMMITTED-də hər statement yeni snapshot görə bilər. REPEATABLE READ eyni transaction snapshot-unu saxlayır, bu PostgreSQL nümayişində phantom count-u da göstərmir. SERIALIZABLE əlavə olaraq serial ardıcıllıqla uyğun olmayan transaction-ları rədd edə bilər; SQLSTATE 40001 olduqda bütün transaction yenidən sınanmalıdır. Hər SERIALIZABLE oxumasında yazanın bloklanması və ya exception gözlənilmir.

**mappedBy, cascade, orphanRemoval nədir?** mappedBy əlaqənin sahib olmayan tərəfini göstərir. cascade seçilmiş entity əməliyyatlarını əlaqəyə ötürür. orphanRemoval əlaqədən çıxarılan uşaq entity-ni silir. ManyToMany-də qarşı tərəfi səhvən silməmək üçün REMOVE cascade ehtiyatla seçilir.

## 7. Postman və təqdimetmə

Import faylı: `School_API.postman_collection.json` (19 sorğu).
Cache (2), Transactions (3), Isolation (2) qovluqları var. Query cache ölkəsi United Kingdom olaraq saxlanılıb.
`My Collection.postman_collection.json` köhnə ayrıca fayldır; bu tapşırıq üçün School_API faylını import edin.

Təqdimetmədən əvvəl qalanlar: tam sualların uyğunluğunu yoxlamaq, rollback məhdudiyyətini razılaşdırıb həll etmək, SQL isolation skriptini canlı bazada işlətmək və GitHub PR-ni açmaq. Bu maddələr bitmədən bütün tapşırığın hazır olduğu iddia edilmir.
