package com.cloud.controller;

import com.cloud.dao.mapper.LocationMapper;
import com.cloud.dao.model.Location;
import com.cloud.service.HttpCommonService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengxin on 2018/8/7.
 */
@RestController
public class gaode {

    @Autowired
    LocationMapper locationMapper;

    @Autowired
    HttpCommonService httpCommonService;


    /*public static void main(String[] args){
        String path = "C:\\Users\\zhengxin\\Desktop\\location\\test.xlsx";
        try {
            insert(readXlsx(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @RequestMapping("/insert_location")
    public String insert(){
        String path = "C:\\Users\\zhengxin\\Desktop\\location\\test.xlsx";
        try {
            //insert(readXlsx(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "hello";
    }

    @RequestMapping("/query")
    public String query(){
        StringBuilder sb = new StringBuilder("https://restapi.amap.com/v3/geocode/geo?address=");

        String result = httpCommonService.executeRequest("https://restapi.amap.com/v3/geocode/geo?address=北京市朝阳区阜通东大街6号&key=9ef3db13dabbd1cda6eecc9cc3329981",
                new HeadAndParamsMap(), HttpMethod.GET);
        JSONObject json = JSONObject.fromObject(result);
        JSONArray dataArr = json.getJSONArray("geocodes");
        if (dataArr.size()>0) {
            JSONObject jsonObject = dataArr.getJSONObject(0);
            String address = String.valueOf(jsonObject.get("formatted_address"));
            String location = String.valueOf(jsonObject.get("location"));
            String level = String.valueOf(jsonObject.get("level"));
        }
        return result;
    }

    @RequestMapping("/transform")
    public String transform(){
        long starttime = System.currentTimeMillis();
        int num = 0;
        List<Location> locations = locationMapper.selectAll();
        try {

            for (Location location : locations) {

                if (location.getGaodeName() != null && !location.getGaodeName().equals("")) {
                    continue;
                }

                String city = location.getPrefectural();
                String count = location.getCounty();
                String countName = location.getCommunityNamw();
                StringBuilder sb = new StringBuilder("https://restapi.amap.com/v3/geocode/geo");
                sb.append("?address=").append(count).append(countName).append("&city=").append(city).append("&key=9ef3db13dabbd1cda6eecc9cc3329981");

                String result = httpCommonService.executeRequest(sb.toString(),
                        new HeadAndParamsMap(), HttpMethod.GET);
                JSONObject json = JSONObject.fromObject(result);
                if (!json.containsKey("geocodes")) {
                    continue;
                }
                JSONArray dataArr = json.getJSONArray("geocodes");
                if (dataArr.size() > 0) {
                    JSONObject jsonObject = dataArr.getJSONObject(0);
                    String address = String.valueOf(jsonObject.get("formatted_address"));
                    String lat = String.valueOf(jsonObject.get("location"));
                    String level = String.valueOf(jsonObject.get("level"));

                    location.setGaodeName(address);
                    location.setLongitude(lat.split(",")[0]);
                    location.setLatitude(lat.split(",")[1]);
                    location.setLevel(level);

                    locationMapper.updateByPrimaryKey(location);
                    System.out.println("当前处理项:" + num++);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }finally {
            transform();
        }
        System.out.println((System.currentTimeMillis()-starttime)/1000/60);
        return "Mission Accomplished";
    }

    public static List<List<String>> readXlsx(String filePath) throws Exception {
        InputStream is = new FileInputStream(filePath);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        List<List<String>> result = new ArrayList<>();
        //循环每一页，并处理当前的循环页
        for (Sheet sheet : xssfWorkbook) {
            if (sheet == null) {
                continue;
            }
            for (int rowNum = 1; rowNum <= sheet.getLastRowNum(); rowNum++) {
                Row row = sheet.getRow(rowNum);//Row表示每一行的数据
                int minColIx = row.getFirstCellNum();
                int maxColIx = row.getLastCellNum();
                List<String> rowList = new ArrayList<>();
                //遍历该行，并获取每一个cell的数据
                for (int colIx = minColIx; colIx < maxColIx; colIx++) {
                    Cell cell = row.getCell(colIx);
                    if (cell == null) {
                        continue;
                    }
                    rowList.add(cell.toString());
                }
                result.add(rowList);
            }
        }
        return result;
    }

    public void insert(List<List<String>> lists){
        List<Location> locations = new ArrayList<>();
        for(List list:lists){
            Location location = new Location();
            location.setPrefectural(String.valueOf(list.get(0)));
            location.setCounty(String.valueOf(list.get(1)));
            location.setCommunityNamw(String.valueOf(list.get(2)));

            locationMapper.insert(location);
            locations.add(location);
        }

    }

    @RequestMapping("/write")
    public String write(){
        List<String>latList = new ArrayList<>();
        List<Location> cleanLocations = new ArrayList<>();
        List<Location> locations = locationMapper.selectAll();
        for (Location location:locations) {
            String latlog="";
            if (location.getLongitude()!=null && location.getLatitude()!=null) {
                latlog = location.getLongitude()+location.getLatitude();
            }else{
                continue;
            }
            if (latlog.equals("")) {
                continue;
            }
            if (latList.size()==0) {
                latList.add(latlog);
            }
            if (cleanLocations.size()==0) {
                cleanLocations.add(location);
            }
            boolean sing = false;
            for (int i = 0;i<latList.size();i++) {
                String a = latList.get(i);
                if (latlog==null || latlog.equals("")) {
                    break;
                }
                if (a.equals(latlog)) {
                    sing = true;
                    break;
                }
            }
            if (!sing) {
                cleanLocations.add(location);
                latList.add(latlog);
                sing=false;
            }
        }

        //parString(cleanLocations);
        parStringForMaker(cleanLocations);
        return "hello";
    }

    public static void main(String args[]){
        String txt = "var points = [{\"lnglat\":[\"113.864691\",\"22.942327\"]},{\"lnglat\":[\"113.370643\",\"22.938827\"]}]";
        contentToTxt("C:\\Users\\zhengxin\\Desktop\\location\\1.txt",txt);
    }

    public void parString(List<Location> list){
        String separ = "\"";
        String com = ",";
        String mao = ":";
        String leftbrace = "{";
        String rightbrace = "}";
        String mleftbrace = "[";
        String mrightbrace = "]";
        String lnglat = "lnglat";

        StringBuilder sb = new StringBuilder("var points = [");
        for (Location location:list) {
            String lon = location.getLongitude();
            String lat = location.getLatitude();
            sb.append(leftbrace).append(separ).append(lnglat).append(separ).append(mao).append(mleftbrace).append(separ)
            .append(lon).append(separ).append(com).append(separ).append(lat).append(separ).append(mrightbrace).append(rightbrace)
            .append(com);
        }
        sb.append(mrightbrace);
        contentToTxt("C:\\Users\\zhengxin\\Desktop\\location\\1.txt",sb.toString());
    }

    public void parStringForMaker(List<Location> list){
        String separ = "\"";
        String com = ",";
        String mao = ":";
        String leftbrace = "{";
        String rightbrace = "}";
        String mleftbrace = "[";
        String mrightbrace = "]";
        String lnglat = "lnglat";

        int num=0;
        StringBuilder sb = new StringBuilder("var provinces = [");
        for (Location location:list) {
            String name = location.getCommunityNamw();
            String center = location.getLongitude()+","+ location.getLatitude();
            String type = location.getLevel();

            sb.append(leftbrace).append(separ).append("name").append(separ).append(mao).append(separ).append(name).append(separ)
                    .append(com).append(separ).append("center").append(separ).append(mao).append(separ).append(center).append(separ)
                    .append(com).append(separ).append("type").append(separ).append(mao).append(separ).append(type).append(separ).append(com).append(separ)
                    .append("subDistricts").append(separ).append(mao).append("[]").append(rightbrace).append(com);

//            if (num==3) {
//                break;
//            }
//            num++;
        }
        sb.append(mrightbrace);
        contentToTxt("C:\\Users\\zhengxin\\Desktop\\location\\maker.txt",sb.toString());
    }

    public static void contentToTxt(String filePath, String content) {
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath),true));
            writer.write(content);
            writer.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

