package models;

import java.util.TreeMap;

public class ReadCategoryProduct {

    //準備好產品清單  
    public static TreeMap<String, Product> readProduct() {
        //read_product_from_file(); //從檔案或資料庫讀入產品菜單資訊
        //放所有產品  產品編號  產品物件
        TreeMap<String, Product> product_dict = new TreeMap<>();
        String[][] product_array = {
            // 葉菜類
            {"p-l-101", "葉菜", "高麗菜", "22.7", "cabbage.jpg", "產品描述"},
            {"p-l-102", "葉菜", "大白菜", "39", "chinese-cabbage.jpg", "產品描述"},
            {"p-l-103", "葉菜", "萵苣", "36.1", "lettuce.png", "產品描述"},
            {"p-l-104", "葉菜", "地瓜葉", "29.4", "sweetPotatoLeaves.jpg", "產品描述"},
            {"p-l-105", "葉菜", "芥藍菜", "39.5", "Chinese-kale.jpg", "產品描述"},
            {"p-l-106", "葉菜", "青江菜", "48.6", "spoon-cabbage.jpg", "產品描述"},
            {"p-l-107", "葉菜", "菠菜", "75.6", "spinach.jpg", "產品描述"},
            {"p-l-108", "葉菜", "芫荽(香菜)", "109", "coriander.jpg", "產品描述"},
            // ----------
            // 水生類
            {"p-w-100", "水生", "綠蘆筍", "149.2", "green-asparagus.jpg", "產品描述"},
            {"p-w-101", "水生", "白蘆筍", "147.5", "white-asparagus.jpg", "產品描述"},
            {"p-w-102", "水生", "綠豆芽", "13.2", "green-bean-sprouts.jpg", "產品描述"},
            {"p-w-103", "水生", "黃豆芽", "20", "yellow-bean-sprouts.jpg", "產品描述"},
            {"p-w-104", "水生", "豌豆芽", "61.8", "pea-sprouts.jpeg", "產品描述"},
            {"p-w-105", "水生", "筊白筍", "71.4", "Water-Bamboo.jpg", "產品描述"},
            {"p-w-106", "水生", "水蓮", "83.9", "white-water-snowflake.jpg", "產品描述"},
            {"p-w-107", "水生", "蕹菜(空心菜)", "69", "water-spinach.jpg", "產品描述"},
            {"p-w-108", "水生", "芹菜", "49.2", "celery.jpg", "產品描述"},
            {"p-w-109", "水生", "菱角(生)", "100", "Trapa.jpg", "產品描述"},
            {"p-w-110", "水生", "菱角(去殼)", "100", "Water-chestnuts-peeled.jpg", "產品描述"},
            {"p-w-111", "水生", "西洋菜", "30", "watercress.jpg", "產品描述"},
            {"p-w-112", "水生", "苜蓿芽", "65", "alfalfa-sprouts.jpg", "產品描述"},
            {"p-w-113", "水生", "蓮藕", "55.8", "lotus-root.jpg", "產品描述"},
            {"p-w-114", "水生", "蓮藕-蓮子", "268.8", "lotus-seeds.jpg", "產品描述"},
            {"p-w-115", "水生", "海帶", "46.5", "kelp.jpg", "產品描述"},
            
            // ----------
            
            
            
//            {"p-t-112", "茶飲", "紅茶", "45", "blacktea.jpg", "產品描述"},
//            {"p-t-113", "茶飲", "綠茶", "45", "greentea.jpg", "產品描述"},
//            {"p-t-114", "咖啡", "咖啡加珍珠", "70", "perlmilktea.jpg", "產品描述"}
        }; 

        //一筆放入字典變數product_dict中
        for (String[] item : product_array) {
            Product product = new Product(
                    item[0], 
                    item[1], 
                    item[2], 
//                    Integer.parseInt(item[3]), //價格轉為int
                    Double.parseDouble(item[3]), //價格轉為double
                    item[4], 
                    item[5]);
            //將這一筆放入字典變數product_dict中 
            product_dict.put(product.getProduct_id(), product);
        }
        return product_dict; 
    }
}