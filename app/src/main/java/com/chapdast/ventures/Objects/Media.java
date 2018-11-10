package com.chapdast.ventures.Objects;

public class Media {
    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    private int mid;
    private String type;
    private String thumb;
    private String link;
    private String name;
    private String sub;

    public Media(String type, String thumb, String link, String name, String level, String sub,int mid){
            setLink(link);
            setName(name);
            setSub(sub);
            setThumb(thumb);
            setType(type);
            setMid(mid);
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String GetFileName(){
        return "."+String.valueOf(this.mid)+ this.name.trim()+".hgm";
    }




}
