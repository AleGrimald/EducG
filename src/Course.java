/** Modelo de datos para un curso de programación. */
public class Course {

    private final String title;
    private final String description;
    private final String duration;
    private final String[] topics;
    private final String emoji;

    public Course(String emoji, String title, String description,
                  String duration, String... topics) {
        this.emoji       = emoji;
        this.title       = title;
        this.description = description;
        this.duration    = duration;
        this.topics      = topics;
    }

    public String   getEmoji()       { return emoji; }
    public String   getTitle()       { return title; }
    public String   getDescription() { return description; }
    public String   getDuration()    { return duration; }
    public String[] getTopics()      { return topics; }
}
