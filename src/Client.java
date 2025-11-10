public class Client {
    private String firstName;
    private String lastName;
    private String id;
    private int age;

    public Client(String firstName, String lastName, String id, int age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.id = id;
        this.age = age;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return getFullName() + " (ID: " + id + ", Age: " + age + ")";
    }
}
