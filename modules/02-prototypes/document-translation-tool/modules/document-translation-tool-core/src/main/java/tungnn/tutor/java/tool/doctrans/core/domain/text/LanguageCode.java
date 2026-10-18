package tungnn.tutor.java.tool.doctrans.core.domain.text;

public enum LanguageCode {
  VI("Vietnamese", "VN", "Vietnam"),
  JA("Japanese", "JP", "Japan"),
  EN("English", "US", "United States"),
  ZH("Chinese", "CN", "China");

  private final String languageName;
  private final String countryCode;
  private final String countryName;

  LanguageCode(String languageName, String countryCode, String countryName) {
    this.languageName = languageName;
    this.countryCode = countryCode;
    this.countryName = countryName;
  }

  public String languageName() {
    return languageName;
  }

  public String countryCode() {
    return countryCode;
  }

  public String countryName() {
    return countryName;
  }
}
