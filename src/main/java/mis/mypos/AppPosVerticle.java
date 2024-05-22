package mis.mypos;

import java.util.TreeMap;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import models.OrderDetail;
import models.Product;

import models.ReadCategoryProduct;

public class AppPosVerticle extends Application{

    //1.宣告全域變數)並取得三種菜單的磁磚窗格，隨時被取用。
    private TilePane menuLeaf = getProductCategoryMenu("葉菜");
    private TilePane menuTea = getProductCategoryMenu("水生");
    //private TilePane menuCoffee = getProductCategoryMenu("咖啡");

    //2.宣告一個容器(全域變數) menuContainerPane，裝不同種類的菜單，菜單類別選擇按鈕被按下，立即置放該種類的菜單。
    private VBox menuContainerPane = new VBox();

    //3.ObservableList    order_list有新增或刪除都會處動table的更新，也就是發生任何改變時都被通知
    private ObservableList<OrderDetail> order_list;

    //4.產品字典
    private TreeMap<String, Product> product_dict = ReadCategoryProduct.readProduct();

    //5.顯示訂單詳情表格
    private TableView table;

    //6.顯示訂單詳情白板
    private TextArea display = new TextArea();
    
//以下都是方法模塊的定義-------------------------------------------------------------------
    
    //先將需要呼叫計算金額的方法準備好
    //計算總金額
    public void checkTotal() {
        double total = 0;
        //將所有的訂單顯示在表格   計算總金額
        for (OrderDetail od : order_list) {
            //加總
            total += od.getProduct_price() * od.getQuantity();
        }

        //顯示總金額於display
        String totalmsg = String.format("%s %d\n", "總金額:", Math.round(total));
        display.setText(totalmsg);
    }// checkTotal

    //加入購物車 檢查是否有重複的飲料
    public void addToCart(String item_id) {

        boolean duplication = false;
        for (int i = 0; i < order_list.size(); i++) {
            if (order_list.get(i).getProduct_id().equals(item_id)) {
                int qty = order_list.get(i).getQuantity() + 1; //拿出qty並+1
                order_list.get(i).setQuantity(qty);
                duplication = true;
                table.refresh();
                checkTotal();
                System.out.println(item_id + "此筆已經加入購物車，數量+1");
                break;
            }
        }
        if (!duplication) { //若是新項目則新增這一筆
            OrderDetail new_ord = new OrderDetail(
                    item_id,
                    product_dict.get(item_id).getName(),
                    product_dict.get(item_id).getPrice(),
                    1);
            order_list.add(new_ord);
            System.out.println(item_id);

            checkTotal(); //更新數量
        }
    } // addToCart

//跟視窗有關的元件-------------------------------------------------------------------
    // 產品菜單磁磚窗格，置放你需要用到的菜單
    public TilePane getProductCategoryMenu(String category) {

        //取得產品清單
        TreeMap<String, Product> product_dict = ReadCategoryProduct.readProduct();
        //磁磚窗格
        TilePane category_menu = new TilePane(); //
        category_menu.setVgap(10);  //垂直間隙
        category_menu.setHgap(10);
        //設定一個 row有4個columns，放不下就放到下一個row
//        category_menu.setPrefColumns(4);
        category_menu.setPrefColumns(6);    

        //將產品清單內容一一置放入產品菜單磁磚窗格
        for (String item_id : product_dict.keySet()) {

            if (product_dict.get(item_id).getCategory().equals(category)) {
                //定義新增一筆按鈕
                Button btn = new Button();

                //width, height 按鈕外框的大小，你要自行調整，讓它美觀。沒有設定外框會大小不一不好看
                btn.setPrefSize(120, 120);
                //btn.setText(product_dict.get(item_id).getName()); //不要顯示文字，顯示圖片就好

                //按鈕元件顯示圖片Creating a graphic (image)
                Image img = new Image("images/" + product_dict.get(item_id).getPhoto()); //讀出圖片
                ImageView imgview = new ImageView(img);//圖片顯示物件
                imgview.setFitHeight(80); //設定圖片高度，你要自行調整，讓它美觀
                imgview.setFitWidth(100);
//                imgview.setPreserveRatio(true); //圖片的寬高比維持

                //Setting a graphic to the button
                btn.setGraphic(imgview); //按鈕元件顯示圖片
                category_menu.getChildren().add(btn);  //放入菜單磁磚窗格

                //定義按鈕事件-->點選一次，就加入購物車，再點選一次，數量要+1
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        //新增一筆訂單到order_list (ArrayList)
                        //order_list.add(new Order("p-109", "新增的果汁", 30, 1));
                        addToCart(item_id);
                        System.out.println(product_dict.get(item_id).getName());
                    }
                });
            }
        }
        return category_menu;
    }

    //1.用一個窗格置放菜單類別選擇按鈕(可以用磁磚窗格最方便)，置放於主視窗的最上方區域。
    public TilePane getMenuSelectionContainer() {
        // 塞入菜單區塊 預設為果汁類
        menuContainerPane.getChildren().add(menuLeaf);
        System.out.println("啟動App預設布置蔬果菜單");

        //定義"葉菜類"按鈕
        Button btnJuice = new Button();
        btnJuice.setText("葉菜類");
        btnJuice.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                menuContainerPane.getChildren().clear();//先刪除原有的窗格再加入新的類別窗格
                menuContainerPane.getChildren().add(menuLeaf);
            }
        });
        //定義"水生類"按鈕
        Button btnTea = new Button("水生類");
        btnTea.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                menuContainerPane.getChildren().clear();//先刪除原有的窗格再加入新的類別窗格
                menuContainerPane.getChildren().add(menuTea);
            }
        });
        //定義"咖啡類"按鈕
//        Button btnCoffee = new Button("咖啡類");
//        btnCoffee.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent e) {
//                menuContainerPane.getChildren().clear();//先刪除原有的窗格再加入新的類別窗格
//                menuContainerPane.getChildren().add(menuCoffee);
//            }
//        });

        //使用磁磚窗格，放置前面三個按鈕
        TilePane conntainerCategoryMenuBtn = new TilePane();
        //conntainerCategoryMenuBtn.setAlignment(Pos.CENTER_LEFT);
        //conntainerCategoryMenuBtn.setPrefColumns(6); //
        conntainerCategoryMenuBtn.setVgap(10);
        conntainerCategoryMenuBtn.setHgap(10);

        conntainerCategoryMenuBtn.getChildren().add(btnJuice);
        conntainerCategoryMenuBtn.getChildren().add(btnTea);
//        conntainerCategoryMenuBtn.getChildren().add(btnCoffee);

        return conntainerCategoryMenuBtn;
    }

    //表格新增項目刪除項目之操作區塊
    public TilePane getOrderOperationContainer() {
        //定義新增一筆按鈕
        Button btnAdd = new Button();
        btnAdd.setText("新增一筆品項");
        btnAdd.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //新增一筆
                //order_list.add(new Order("p-109", "新增的果汁", 30, 1));
                addToCart("p-j-101");
            }
        });

        //定義刪除一筆按鈕
        Button btnDelete = new Button("刪除一筆");
        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //從table中取得目前被選到的項目
                Object selectedItem = table.getSelectionModel().getSelectedItem();
                //從表格table中或是從order_list刪除這一筆，擇一進行
                //table.getItems().remove(selectedItem);
                order_list.remove(selectedItem);
                checkTotal();
            }
        });

        //定義結帳按鈕
        Button btnCheck = new Button("結帳");
        btnCheck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                //目前沒有功能
                //checkSave();
            }
        });
        //放置任務功能按鈕
        TilePane operationBtnTile = new TilePane();
        //operationBtnTile.setAlignment(Pos.CENTER_LEFT);
        //operationBtnTile.setPrefColumns(6);
        operationBtnTile.setVgap(10);
        operationBtnTile.setHgap(10);

        btnAdd.setDisable(true);
        operationBtnTile.getChildren().add(btnAdd);
        operationBtnTile.getChildren().add(btnDelete);
        operationBtnTile.getChildren().add(btnCheck);

        return operationBtnTile;
    }// getOrderOperationContainer()

    //初始化所有表格相關的元件與事件
    public void initializeOrderTable() {

        //訂單陣列串列初始化FXCollections類別有很多靜態方法可以操作ObservableList的"訂單陣列串列"
        order_list = FXCollections.observableArrayList();
        //前面是空的串列，若已知有兩筆訂單可以這樣設定
        /*
        order_list = FXCollections.observableArrayList(
                new Order("p-101", "葡萄汁", 80, 3),
                new Order("p-102", "番茄汁", 70, 1)
        );        */
        //也可以這樣加入一筆訂單
        //order_list.add(new Order("p-103", "西瓜汁", 80, 3));
        //表格初始化
        table = new TableView();
        table.setEditable(true); //表格允許修改
        table.setPrefHeight(300);
        //表格內置放的資料來自於order_list，依據置放順序顯示
        table.setItems(order_list);

        //table也可以這樣放入訂單
        //table.getItems().add(new Order("p-104", "奇異果汁", 50, 2));
        //定義第一個欄位column"品名"，其值來自於Order物件的某個String變數
        TableColumn<OrderDetail, String> order_item_name = new TableColumn("品名");
        //置放哪個變數值?指定這個欄位 對應到Order的"name"實例變數值
        order_item_name.setCellValueFactory(new PropertyValueFactory("product_name"));

        order_item_name.setPrefWidth(611); //設定欄位寬度
        order_item_name.setMinWidth(100);

//        TableColumn<OrderDetail, Integer> order_item_price = new TableColumn("價格");
        TableColumn<OrderDetail, Double> order_item_price = new TableColumn("價格");
        order_item_price.setCellValueFactory(new PropertyValueFactory("product_price"));

        TableColumn order_item_qty = new TableColumn("數量");
        order_item_qty.setCellValueFactory(new PropertyValueFactory("quantity"));
        //這個欄位值內容可以被修改，因為quantity是整數，因此須將整數轉為字串，才能異動OrderDetail物件，否則會報錯!
        order_item_qty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

        //把3個欄位加入table中
        table.getColumns().addAll(order_item_name, order_item_price, order_item_qty);

        //表格最後一欄是空白，不要顯示!
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        //定義數量欄位被修改後進行那些動作
        order_item_qty.setOnEditCommit(new EventHandler<CellEditEvent>() {
            @Override
            public void handle(CellEditEvent event) {

                int row_num = event.getTablePosition().getRow();//哪一筆被修改
                int new_val = (Integer) event.getNewValue(); //改成甚麼數值 需要將物件轉為整數
                OrderDetail target = (OrderDetail) event.getTableView().getItems().get(row_num); //取得該筆果汁傳參考呼叫
                //修改成新的數值 該筆訂單存放於order_list
                target.setQuantity(new_val);
                //更新總價
                checkTotal();
                System.out.println(order_list.get(row_num).getQuantity()); //顯示修改後的數值
            }
        });
    } // initializeOrderTable()

    @Override
    public void start(Stage stage) throws Exception {

        //根容器 所有的元件都放在裡面container，最後再放進布景中scene，布景再放進舞台中stage
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.getStylesheets().add("/css/bootstrap3.css");

        //塞入菜單選擇區塊
        root.getChildren().add(getMenuSelectionContainer());

        //取得菜單磁磚窗格並放入根容器
        // menuContainerPane.getChildren().add(menuLeaf); //可以在這裡將菜單窗格塞入飲料菜單
        root.getChildren().add(menuContainerPane);

        //塞入增加表格刪除項目操作之容器
        root.getChildren().add(getOrderOperationContainer());

        //塞入表格
        initializeOrderTable(); //表格要初始化
        root.getChildren().add(table);
        
        //塞入白板display
        display.setPrefColumnCount(10);
        root.getChildren().add(display);

        //塞入布景
        Scene scene = new Scene(root);
        stage.setTitle("蔬果行");
        stage.setScene(scene);
        stage.show();
    } // start()

    public static void main(String[] args) {
        launch(args);
    }

}
