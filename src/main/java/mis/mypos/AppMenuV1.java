package mis.mypos;

import java.util.TreeMap;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Product;
import models.ReadCategoryProduct;

public class AppMenuV1 extends Application {

    @Override
    public void start(Stage stage) {
        
         //取得產品清單
        TreeMap<String, Product> product_dict = ReadCategoryProduct.readProduct();
        //磁磚窗格用來置放product menu
        TilePane menu = new TilePane(); //
        menu.setVgap(5);  //垂直間隙
        menu.setHgap(5);
        
        //將產品清單內容一一置放入產品菜單磁磚窗格
        for (String item_id : product_dict.keySet()) {

            //定義新增一筆按鈕
            Button btn = new Button();

            //width, height 按鈕外框的大小，你要自行調整，讓它美觀。沒有設定外框會大小不一不好看
            btn.setPrefSize(120, 120);
            //btn.setText(product_dict.get(item_id).getName()); //不要顯示文字，顯示圖片就好

            //按鈕元件顯示圖片Creating a graphic (image)
            Image img = new Image("/imgs/" + product_dict.get(item_id).getPhoto()); //讀出圖片
            ImageView imgview = new ImageView(img);//圖片顯示物件
            imgview.setFitHeight(80); //設定圖片高度，你要自行調整，讓它美觀
            imgview.setPreserveRatio(true); //圖片的寬高比維持

            //Setting a graphic to the button
            btn.setGraphic(imgview); //按鈕元件顯示圖片
            menu.getChildren().add(btn);  //放入菜單磁磚窗格

            //定義按鈕事件-->點選一次，就加入購物車，再點選一次，數量要+1
            btn.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    //新增一筆訂單到order_list (ArrayList)
                    //addToCart(item_id);
                    //order_list.add(new Order("p-109", "新增的果汁", 30, 1));
                    System.out.println(product_dict.get(item_id).getName());
                }
            });
        } //for loop

        
        
       //根容器所有的元件都放在裡面container(使用Vbox置放)，最後再放進布景中scene，布景再放進舞台中stage
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10, 10, 10, 10)); //外框內墊片
        root.getStylesheets().add("/css/bootstrap3.css");

        root.getChildren().add(menu);
        Scene scene = new Scene(root);
        //Scene scene = new Scene(root, 600,800);
        stage.setTitle("飲料菜單");
        stage.setScene(scene);
        stage.show();


        

    }

    public static void main(String[] args) {
        launch(args);
    }

}
