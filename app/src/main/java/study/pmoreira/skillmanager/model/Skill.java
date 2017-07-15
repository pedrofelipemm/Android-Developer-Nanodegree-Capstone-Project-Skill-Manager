package study.pmoreira.skillmanager.model;

public class Skill {

    private String name;
    private String description;
    private String learnMoreUrl;
    private String pictureUrl;

    public Skill(String name, String description, String learnMoreUrl, String pictureUrl) {
        this.name = name;
        this.description = description;
        this.learnMoreUrl = learnMoreUrl;
        this.pictureUrl = pictureUrl;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLearnMoreUrl() {
        return learnMoreUrl;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }
}
