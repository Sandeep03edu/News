package com.example.news;

public class News {
    // Variable decleration
    private String nTitle;
    private String nImageSrc;
    private String nPubDate;
    private String nCreator;
    private String nDescription;
    private String nContent;
    private String nUrl;
    public News(String gTitle, String gImage, String gPubDate, String gCreator, String gDescription, String gContent, String gUrl){
            nTitle = gTitle;
            nImageSrc = gImage;
            nPubDate = gPubDate;
            nCreator = gCreator;
            nDescription = gDescription;
            nContent = gContent;
            nUrl = gUrl;
    }

    public String getnTitle(){return nTitle;}
    public String getnImageSrc(){return nImageSrc;}
    public String getnPubDate(){return nPubDate;}
    public String getnCreator(){return nCreator;}
    public String getnDescription(){return nDescription;}
    public String getnContent(){return nContent;}
    public String getnUrl(){return nUrl;}

}
