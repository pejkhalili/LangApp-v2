package com.chapdast.ventures.Objects;

public class CategoriesMedia {
    public int getId() {
        return mid;
    }

    public void setId(int mid) {
        this.mid = mid;
    }

    private int mid;

    private String thumb;
    private String name;

    public CategoriesMedia(String thumb, String name, int mid){
            setName(name);
            setThumb(thumb);

            setId(mid);
    }



    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String GetFileName(){
        return "."+String.valueOf(this.mid)+ this.name.trim()+".hgc";
    }




}
