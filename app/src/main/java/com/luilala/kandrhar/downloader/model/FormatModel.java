package com.luilala.kandrhar.downloader.model;

import java.util.ArrayList;
import java.util.List;

public class FormatModel {
    private String title;
    List<NestedFormatModel> nestedFormatModelList ;

    public FormatModel(String title, List<NestedFormatModel> nestedFormatModelList) {
        this.title = title;
//        List<NestedFormatModel> list = new ArrayList<>();
//        int index = 0;
//        list.add(nestedFormatModelList.get(index));
//        for (int i = 1; i < nestedFormatModelList.size(); i++) {
//            if (!list.get(index).getQuality().equals(nestedFormatModelList.get(i).getQuality())){
//                list.add(nestedFormatModelList.get(i));
//                index ++;
//            }
//
//        }
        this.nestedFormatModelList = nestedFormatModelList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<NestedFormatModel> getNestedFormatModelList() {
        return nestedFormatModelList;
    }

    public void setNestedFormatModelList(List<NestedFormatModel> nestedFormatModelList) {
        this.nestedFormatModelList = nestedFormatModelList;
    }
}
