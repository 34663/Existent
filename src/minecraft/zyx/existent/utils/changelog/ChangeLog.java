package zyx.existent.utils.changelog;

public class ChangeLog {
    private String changeName;
    private final ChangelogType type;

    public ChangeLog(String name, ChangelogType type) {
        this.changeName = name;
        this.type = type;

        switch (type) {
            case NONE:
                changeName = ": " + changeName;
                break;
            case ADD:
                changeName = "\2477[\247a+\2477] Added \247f" + changeName;
                break;
            case DELETE:
                changeName = "\2477[\247c-\2477] Delete \247f" + changeName;
                break;
            case IMPROVED:
                changeName = "\2477[\2479/\2477] Improved \247f" + changeName;
                break;
            case FIXED:
                changeName = "\2477[\247b/\2477] Fixed \247f" + changeName;
                break;
            case PROTOTYPE:
                changeName = "\2477[\247e?\2477] Prototype \247f" + changeName;
                break;
            case NEW:
                changeName = "\2477[\2476!\2477] New \247f" + changeName;
                break;
        }
    }

    public String getChangeName() {
        return changeName;
    }

    public ChangelogType getType() {
        return type;
    }
}