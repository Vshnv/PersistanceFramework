# PersistanceFramework
A framework for easier integration with databases.


PF is the simple solution to replacing a lot of unneccessary JDBC usage.

# Example:
Kotlin:
```kotlin
val connector = SQLConnector(SQLDriver.MYSQL, "TestDatabase", "Hostname", "3306", "username", "password")

val persistentRepo: Repository<UUID, TestData> = SQLRepository(connector, "TestTable")

val id = UUID.nameUUIDFromBytes("SIMON".toByteArray())
val personA = TestData(id, "Simon", 30, true)
    
fun onInsert() {
    persistentRepo.with(id) { person ->
        println(person.name)
        println(person.age)
        println(person.isStudent)
    }
}
 persistentRepo.insert(personA) {
    print("Inserted")
    onInsert();
}
```
Java:

```java
Repository<UUID, TestData> persistentRepo = new SQLRepository(UUID.class, TestData.class, connector, "TestTable");
UUID id = UUID.nameUUIDFromBytes("SIMON".getBytes());
TestData personA = new TestData(id,"Simon", 30, true);

persistentRepo.insert(personA, ()-> {
    System.out.print("Inserted");
});



public void onInsert(UUID id, Repository<UUID, TestData> repo) {
    repo.with(id, person -> {
        System.out.println(person.getName());
        System.out.println(person.getAge());
        System.out.println(person.isStudent());
    });
}
```

The TestData class used above:
```java
public class TestData implements Persistable<UUID> {
    private @PersistentKey UUID id;
    private String name;
    private int age;
    private boolean student;

    public TestData(UUID id, String name, int age, boolean student) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.student = student;
    }

    @Override
    public void init(UUID uuid) {
        System.out.println("Loaded data with UUID " + uuid.toString());
    }

    public String getName() {
        return name;
    }
    public int getAge() {
        return age;
    }
    public boolean isStudent() {
        return student;
    }
}
```
