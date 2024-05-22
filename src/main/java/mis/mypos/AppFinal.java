package mis.mypos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import models.OrderDetail;
import models.Product;
import models.ReadCategoryProduct;

public class AppFinal extends Application {

    // 1.宣告全域變數)並取得三種菜單的磁磚窗格，隨時被取用。
    private TilePane menuLeaf = getProductCategoryMenu("葉菜");
    private TilePane menuTea = getProductCategoryMenu("水生");
    // private TilePane menuCoffee = getProductCategoryMenu("咖啡");

    // 2.宣告一個容器(全域變數) menuContainerPane，裝不同種類的菜單，菜單類別選擇按鈕被按下，立即置放該種類的菜單。
    private VBox menuContainerPane = new VBox();

    // 3.ObservableList order_list有新增或刪除都會處動table的更新，也就是發生任何改變時都被通知
    private ObservableList<OrderDetail> order_list;

    // 4.產品字典
    private TreeMap<String, Product> product_dict = ReadCategoryProduct.readProduct();

    // 5.顯示訂單詳情表格
    private TableView<OrderDetail> table;

    // 6.顯示訂單詳情白板
    private TextArea display = new TextArea();
    
    // 7.搜尋框
    private TextField searchField = new TextField();

    // 以下都是方法模塊的定義-------------------------------------------------------------------

    // 先將需要呼叫計算金額的方法準備好
    // 計算總金額
    public void checkTotal() {
        double total = 0;
        // 將所有的訂單顯示在表格 計算總金額
        for (OrderDetail od : order_list) {
            // 加總
            total += od.getProduct_price() * od.getQuantity();
        }

        // 顯示總金額於display
        String totalmsg = String.format("%s %d\n", "總金額:", Math.round(total));
        display.setText(totalmsg);
    }// checkTotal

    // 加入購物車 檢查是否有重複的飲料
    public void addToCart(String item_id, int quantity) {

        boolean duplication = false;
        for (int i = 0; i < order_list.size(); i++) {
            if (order_list.get(i).getProduct_id().equals(item_id)) {
                int qty = order_list.get(i).getQuantity() + 1; // 拿出qty並+1
                order_list.get(i).setQuantity(qty);
                duplication = true;
                table.refresh();
                checkTotal();
                System.out.println(item_id + "此筆已經加入購物車，數量+1");
                break;
            }
        }
        if (!duplication) { // 若是新項目則新增這一筆
            OrderDetail new_ord = new OrderDetail(item_id, product_dict.get(item_id).getName(),
                    product_dict.get(item_id).getPrice(), quantity);
            order_list.add(new_ord);
            System.out.println(item_id);

            checkTotal(); // 更新數量
        }
    } // addToCart

    // 跟視窗有關的元件-------------------------------------------------------------------
    // 產品菜單磁磚窗格，置放你需要用到的菜單
    public TilePane getProductCategoryMenu(String category) {

        // 取得產品清單
        TreeMap<String, Product> product_dict = ReadCategoryProduct.readProduct();
        // 磁磚窗格
        TilePane category_menu = new TilePane(); //
        category_menu.setVgap(10); // 垂直間隙
        category_menu.setHgap(10);
        // 設定一個 row有4個columns，放不下就放到下一個row
        category_menu.setPrefColumns(4);
        category_menu.setStyle("-fx-background-color: #f5f5f5;");
//        category_menu.setStyle("-fx-background-color: #ffffff;");

        // 將產品清單內容一一置放入產品菜單磁磚窗格
        for (String item_id : product_dict.keySet()) {

            if (product_dict.get(item_id).getCategory().equals(category)) {
                // 定義新增一筆按鈕
                Button btn = new Button();

                // width, height 按鈕外框的大小，你要自行調整，讓它美觀。沒有設定外框會大小不一不好看
                btn.setPrefSize(120, 160);

                // 按鈕元件顯示圖片Creating a graphic (image)
                Image img = new Image("images/" + product_dict.get(item_id).getPhoto()); // 讀出圖片
                ImageView imgview = new ImageView(img);// 圖片顯示物件
                imgview.setFitHeight(80); // 設定圖片高度，你要自行調整，讓它美觀
                imgview.setFitWidth(100);

                // 設定按鈕的圖片和文字（包括價格）
                VBox vbox = new VBox();
                vbox.setSpacing(2);
                vbox.setAlignment(Pos.CENTER); // 水平置中
                
                // 添加分隔線
                Separator separator = new Separator();
                separator.setPrefWidth(100);
                separator.setMouseTransparent(true);
//                separator.setStyle("-fx-background-color: #000000; -fx-padding: 0 0 0 0;"); // 調整分隔線樣式
                
                Label nameLabel = new Label(product_dict.get(item_id).getName());
                nameLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
                nameLabel.setAlignment(Pos.TOP_LEFT); // 左上角
                
                Label priceLabel = new Label("$" + product_dict.get(item_id).getPrice());
                priceLabel.setStyle("-fx-font-size: 12;");
                priceLabel.setAlignment(Pos.BOTTOM_RIGHT); // 右下角
                
                
                // 使用 VBox 來放置名稱和價格
                VBox namePriceBox = new VBox();
                namePriceBox.setSpacing(2);
                namePriceBox.setAlignment(Pos.CENTER); // 水平置中
                namePriceBox.setPadding(new Insets(2, 0, 2, 0));
                namePriceBox.setPrefWidth(120); // 設置固定寬度，以便控件能夠對齊
                namePriceBox.setPrefHeight(40); // 設置固定高度，以便控件能夠對齊

                // 添加名稱和價格到 VBox，並設置其位置
                VBox.setMargin(nameLabel, new Insets(0, 0, 0, 0)); // 名稱與左邊界的間距
                VBox.setMargin(priceLabel, new Insets(0, 0, 0, 0)); // 價格與右邊界的間距

                HBox nameBox = new HBox(nameLabel);
                nameBox.setAlignment(Pos.TOP_LEFT); // 名稱左上角
                HBox priceBox = new HBox(priceLabel);
                priceBox.setAlignment(Pos.BOTTOM_RIGHT); // 價格右下角

                namePriceBox.getChildren().addAll(nameBox, priceBox);

                // 設置控件之間的 margin
                VBox.setMargin(separator, new Insets(0, 0, 0, 0)); // 分隔線與其他控件之間的間距

                vbox.getChildren().addAll(imgview, separator, namePriceBox);

                // Setting a graphic to the button
                btn.setGraphic(vbox); // 按鈕元件顯示圖片和文字
                category_menu.getChildren().add(btn); // 放入菜單磁磚窗格

                // 定義按鈕事件-->點選一次，顯示數量輸入框
                btn.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        showProductDetails(item_id); // 顯示數量輸入視窗
                    }
                });
            }
        }
        return category_menu;
    }

    // 1.用一個窗格置放菜單類別選擇按鈕(可以用磁磚窗格最方便)，置放於主視窗的最上方區域。
    public VBox getMenuSelectionContainer() {
        // 塞入菜單區塊 預設為果汁類
        menuContainerPane.getChildren().add(menuLeaf);
        System.out.println("啟動App預設布置蔬果菜單");

        // 定義"葉菜類"按鈕
        Button btnJuice = new Button();
        btnJuice.setText("葉菜類");
        btnJuice.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnJuice.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                menuContainerPane.getChildren().clear();// 先刪除原有的窗格再加入新的類別窗格
                menuContainerPane.getChildren().add(menuLeaf);
            }
        });
        btnJuice.setOnMouseEntered(event -> btnJuice.setStyle("-fx-background-color: #45A049; -fx-text-fill: white;"));
        btnJuice.setOnMouseExited(event -> btnJuice.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"));
        // 定義"水生類"按鈕
        Button btnTea = new Button("水生類");
        btnTea.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnTea.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                menuContainerPane.getChildren().clear();// 先刪除原有的窗格再加入新的類別窗格
                menuContainerPane.getChildren().add(menuTea);
            }
        });
        btnTea.setOnMouseEntered(event -> btnTea.setStyle("-fx-background-color: #1E88E5; -fx-text-fill: white;"));
        btnTea.setOnMouseExited(event -> btnTea.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;"));
        // 定義"咖啡類"按鈕
        // Button btnCoffee = new Button("咖啡類");
        // btnCoffee.setOnAction(new EventHandler<ActionEvent>() {
        // @Override
        // public void handle(ActionEvent e) {
        // menuContainerPane.getChildren().clear();//先刪除原有的窗格再加入新的類別窗格
        // menuContainerPane.getChildren().add(menuCoffee);
        // }
        // });
        
        // 搜尋框和按鈕
        searchField.setPromptText("搜尋產品名稱");
        searchField.setPrefWidth(150);
//        Button searchButton = new Button("搜尋");
//        searchButton.setOnAction(new EventHandler<ActionEvent>() {
//            @Override
//            public void handle(ActionEvent event) {
//                String keyword = searchField.getText();
//                searchProducts(keyword);
//            }
//        });
        
        //添加搜尋框變更監聽器
        searchField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                searchProducts(newValue);
            }
        });
        
        HBox searchBox = new HBox(5, searchField);
        searchBox.setAlignment(Pos.CENTER_RIGHT);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // 使用磁磚窗格，放置前面兩個按鈕
        HBox containerCategoryMenuBtn = new HBox(10);
        containerCategoryMenuBtn.setAlignment(Pos.CENTER_LEFT);  // 將分類按鈕對齊到左邊
        containerCategoryMenuBtn.getChildren().addAll(btnJuice, btnTea, spacer, searchBox);

        VBox menuSelectionContainer = new VBox(10, containerCategoryMenuBtn);
        return menuSelectionContainer;
    }
    
    // 搜尋產品名稱
    private void searchProducts(String keyword) {
        menuContainerPane.getChildren().clear();
        if (keyword.isEmpty()) {
            menuContainerPane.getChildren().add(menuLeaf);
        } else {
            TilePane filteredMenu = new TilePane();
            filteredMenu.setVgap(10);
            filteredMenu.setHgap(10);
            filteredMenu.setPrefColumns(4);
            for (String item_id : product_dict.keySet()) {
                if (product_dict.get(item_id).getName().contains(keyword)) {
                    Button btn = createProductButton(item_id);
                    filteredMenu.getChildren().add(btn);
                }
            }
            menuContainerPane.getChildren().add(filteredMenu);
        }
    }

    // 創建產品按鈕
    private Button createProductButton(String item_id) {
        Button btn = new Button();
        btn.setPrefSize(120, 160);

        Image img = new Image("images/" + product_dict.get(item_id).getPhoto());
        ImageView imgview = new ImageView(img);
        imgview.setFitHeight(80);
        imgview.setFitWidth(100);

        VBox vbox = new VBox();
        vbox.setSpacing(2);
        vbox.setAlignment(Pos.CENTER);

        Separator separator = new Separator();
        separator.setPrefWidth(100);

        Label nameLabel = new Label(product_dict.get(item_id).getName());
        nameLabel.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
        nameLabel.setAlignment(Pos.TOP_LEFT);

        Label priceLabel = new Label("$" + product_dict.get(item_id).getPrice());
        priceLabel.setStyle("-fx-font-size: 12;");
        priceLabel.setAlignment(Pos.BOTTOM_RIGHT);

        VBox namePriceBox = new VBox();
        namePriceBox.setSpacing(2);
        namePriceBox.setAlignment(Pos.CENTER);
        namePriceBox.setPadding(new Insets(2, 0, 2, 0));
        namePriceBox.setPrefWidth(120);
        namePriceBox.setPrefHeight(40);

        VBox.setMargin(nameLabel, new Insets(0, 0, 0, 0));
        VBox.setMargin(priceLabel, new Insets(0, 0, 0, 0));

        HBox nameBox = new HBox(nameLabel);
        nameBox.setAlignment(Pos.TOP_LEFT);
        HBox priceBox = new HBox(priceLabel);
        priceBox.setAlignment(Pos.BOTTOM_RIGHT);

        namePriceBox.getChildren().addAll(nameBox, priceBox);
        VBox.setMargin(separator, new Insets(0, 0, 0, 0));

        vbox.getChildren().addAll(imgview, separator, namePriceBox);
        btn.setGraphic(vbox);

        btn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
//                addToCart(item_id);
                showProductDetails(item_id); // 顯示新視窗
            }
        });

        return btn;
    }
    
    // 顯示產品詳情的視窗
    private void showProductDetails(String item_id) {
        Stage newStage = new Stage();
        newStage.setTitle("商品詳情");

        // 左側部分：圖片、名稱、價格
        VBox leftBox = new VBox(20);
        leftBox.setPadding(new Insets(10));
        leftBox.setSpacing(25);

        Product product = product_dict.get(item_id);

        ImageView imgView = new ImageView(new Image("images/" + product.getPhoto()));
        imgView.setFitHeight(200);
        imgView.setFitWidth(200);

        Label nameLabel = new Label("商品名稱: " + product.getName());
        nameLabel.setStyle("-fx-font-size: 18px;"); // 放大文字

        Label priceLabel = new Label("價格: $" + product.getPrice());
        priceLabel.setStyle("-fx-font-size: 18px;"); // 放大文字

        leftBox.getChildren().addAll(imgView, nameLabel, priceLabel);

        // 右側部分：數量輸入和數字鍵盤
        VBox rightBox = new VBox();
        rightBox.setPadding(new Insets(10));
        rightBox.setSpacing(10);

        Label quantityLabel = new Label("購買數量:");
        TextField quantityField = new TextField();
        quantityField.setPromptText("輸入數量");

        quantityField.setStyle("-fx-font-size: 14px;"); // 設置字體大小
        quantityField.setPrefWidth(100); // 設置購買數量方格的寬度

        GridPane keypad = new GridPane();
        keypad.setHgap(10);
        keypad.setVgap(10);
        String[] keys = {"7", "8", "9", "4", "5", "6", "1", "2", "3", "0"};
        int row = 0;
        int col = 0;
        for (String key : keys) {
            Button keyBtn = new Button(key);
            keyBtn.setPrefSize(50, 50);
            keyBtn.setOnAction(e -> quantityField.appendText(key));
            keypad.add(keyBtn, col, row);
            col++;
            if (col > 2) {
                col = 0;
                row++;
            }
        }

        // 添加 backspace 圖案按鈕
        Button backspaceBtn = new Button();
        ImageView backspaceImage = new ImageView(new Image("images/backspace.png")); // 假設backspace圖案放在images文件夾中
        backspaceImage.setFitHeight(20);
        backspaceImage.setFitWidth(20);
        backspaceBtn.setGraphic(backspaceImage);
        backspaceBtn.setPrefSize(50, 50);
        backspaceBtn.setOnAction(e -> {
            String currentText = quantityField.getText();
            if (!currentText.isEmpty()) {
                quantityField.setText(currentText.substring(0, currentText.length() - 1));
            }
        });
        keypad.add(backspaceBtn, col, row);

        // 添加清空按鈕
        Button clearBtn = new Button("C");
        clearBtn.setPrefSize(50, 50);
        clearBtn.setOnAction(e -> quantityField.clear());
        keypad.add(clearBtn, col + 1, row);

        // 按鈕區域
        HBox buttonBox = new HBox(100);
        buttonBox.setPadding(new Insets(10,0,10,0));
        buttonBox.setAlignment(Pos.CENTER);

        Button addButton = new Button("加入購物車");
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setMinSize(70, 30);
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String quantityText = quantityField.getText();
                int quantity;
                try {
                    quantity = Integer.parseInt(quantityText);
                    if (quantity <= 0) {
                        showAlert("請輸入大於0的數字");
                        return;
                    }
                    addToCart(item_id, quantity);
                    newStage.close(); // 關閉視窗
                } catch (NumberFormatException e) {
                    showAlert("請輸入有效的數字");
                }
            }
        });

        Button cancelButton = new Button("取消");
        cancelButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        cancelButton.setMinSize(70, 30); // 設置按鈕的最小寬度和高度
        cancelButton.setOnAction(e -> newStage.close());

        HBox.setHgrow(addButton, Priority.ALWAYS);
        HBox.setHgrow(cancelButton, Priority.ALWAYS);
        buttonBox.getChildren().addAll(cancelButton, addButton);
        HBox.setMargin(addButton, new Insets(0, 0, 0, 10));
        HBox.setMargin(cancelButton, new Insets(0, 10, 0, 0));
        buttonBox.setAlignment(Pos.BOTTOM_CENTER);

        rightBox.getChildren().addAll(quantityLabel, quantityField, keypad);

        HBox root = new HBox();
        root.setPadding(new Insets(10));
        root.setSpacing(20);
        root.getChildren().addAll(leftBox, rightBox);
        root.setAlignment(Pos.BOTTOM_CENTER);
//        root.setStyle("-fx-background-color: #bcffad;");

        VBox mainBox = new VBox();
        mainBox.getChildren().addAll(root, buttonBox);
        VBox.setVgrow(root, Priority.ALWAYS);
//        mainBox.setStyle("-fx-background-color: #e6ffe1;");
        
        // 將buttonBox上移
        VBox.setMargin(buttonBox, new Insets(0, 0, 15, 0)); 

        Scene scene = new Scene(mainBox, 500, 400);
        newStage.setScene(scene);
        newStage.show();
    }

    
    private void showAlert(String message) {
        Stage alertStage = new Stage();
        alertStage.setTitle("通知");

        VBox alertBox = new VBox();
        alertBox.setPadding(new Insets(10));
        alertBox.setSpacing(10);
        alertBox.setAlignment(Pos.CENTER);

        Label alertLabel = new Label(message);
        alertLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;"); // 設置文字大小和顏色

        Button okButton = new Button("確定");
        okButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;"); // 設置按鈕樣式
        okButton.setOnAction(e -> alertStage.close());

        alertBox.getChildren().addAll(alertLabel, okButton);

        Scene scene = new Scene(alertBox, 250, 150);
        alertStage.setScene(scene);
        alertStage.show();
    }

    // 表格新增項目刪除項目之操作區塊
    public TilePane getOrderOperationContainer() {
        // 定義新增一筆按鈕
        // Button btnAdd = new Button();
        // btnAdd.setText("新增一筆品項");
        // btnAdd.setOnAction(new EventHandler<ActionEvent>() {
        // @Override
        // public void handle(ActionEvent event) {
        // // 新增一筆
        // // order_list.add(new Order("p-109", "新增的果汁", 30, 1));
        // addToCart("p-j-101");
        // }
        // });

        // 定義刪除一筆按鈕
        Button btnDelete = new Button("刪除一筆");
        btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        btnDelete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                // 從table中取得目前被選到的項目
                Object selectedItem = table.getSelectionModel().getSelectedItem();
                // 從表格table中或是從order_list刪除這一筆，擇一進行
                // table.getItems().remove(selectedItem);
                order_list.remove(selectedItem);
                checkTotal();
            }
        });
        btnDelete.setOnMouseEntered(event -> btnDelete.setStyle("-fx-background-color: #e53935; -fx-text-fill: white;"));
        btnDelete.setOnMouseExited(event -> btnDelete.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;"));

        // 定義結帳按鈕
        Button btnCheck = new Button("結帳");
        btnCheck.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnCheck.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                showCheckoutWindow(); // 顯示結帳視窗
            }
        });
        btnCheck.setOnMouseEntered(event -> btnCheck.setStyle("-fx-background-color: #45A049; -fx-text-fill: white;"));
        btnCheck.setOnMouseExited(event -> btnCheck.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"));
        // 放置任務功能按鈕
        TilePane operationBtnTile = new TilePane();
        // operationBtnTile.setAlignment(Pos.CENTER_LEFT);
        // operationBtnTile.setPrefColumns(6);
        operationBtnTile.setVgap(10);
        operationBtnTile.setHgap(10);

        // btnAdd.setDisable(true);
        // operationBtnTile.getChildren().add(btnAdd);
        operationBtnTile.getChildren().add(btnDelete);
        operationBtnTile.getChildren().add(btnCheck);

        return operationBtnTile;
    }// getOrderOperationContainer()
    
    // 顯示結帳視窗的方法
    private void showCheckoutWindow() {
        Stage checkoutStage = new Stage();
        checkoutStage.setTitle("結帳");

        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        root.setAlignment(Pos.CENTER);

        // 添加時間、帳號和名稱
        HBox headerBox = new HBox();
        headerBox.setSpacing(20);
        headerBox.setAlignment(Pos.CENTER);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String currentTime = LocalDateTime.now().format(formatter);

        Label timeLabel = new Label("時間：" + currentTime);
        timeLabel.setStyle("-fx-font-size: 14px;");

        Label accountLabel = new Label("會員帳號：" + "test@gmail.com");
        accountLabel.setStyle("-fx-font-size: 14px;");

        Label nameLabel = new Label("名稱：" + "test");
        nameLabel.setStyle("-fx-font-size: 14px;");

        headerBox.getChildren().addAll(timeLabel, accountLabel, nameLabel);

        // 定義TableView來顯示購物車內容
        TableView<OrderDetail> checkoutTable = new TableView<>();
        checkoutTable.setEditable(false);

        // 定義欄位
        TableColumn<OrderDetail, String> nameColumn = new TableColumn<>("品名");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("product_name"));
        nameColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<OrderDetail, Double> priceColumn = new TableColumn<>("價格");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("product_price"));
        priceColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<OrderDetail, Integer> quantityColumn = new TableColumn<>("數量");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        quantityColumn.setStyle("-fx-alignment: CENTER;");

        TableColumn<OrderDetail, String> subtotalColumn = new TableColumn<>("合計");
        subtotalColumn.setCellValueFactory(cellData -> {
            OrderDetail orderDetail = cellData.getValue();
            double subtotal = orderDetail.getProduct_price() * orderDetail.getQuantity();
            return new SimpleObjectProperty<>(String.valueOf(Math.round(subtotal)));
        }); 
        subtotalColumn.setStyle("-fx-alignment: CENTER;");

        // 將欄位加入TableView
        checkoutTable.getColumns().addAll(nameColumn, priceColumn, quantityColumn, subtotalColumn);

        // 設定TableView的資料來源
        checkoutTable.setItems(order_list);

        // 設置TableView的列大小策略以自動調整行數
        checkoutTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 計算TableView的高度，避免顯示多餘的空白行
        checkoutTable.setFixedCellSize(25); // 假設每行的高度是25
        int numberOfRows = order_list.size();
        checkoutTable.setPrefHeight(numberOfRows * checkoutTable.getFixedCellSize() + 28); // 28是表頭的高度

        // 計算總計，四捨五入並轉換為整數
        double total = 0;
        for (OrderDetail od : order_list) {
            total += od.getProduct_price() * od.getQuantity();
        }
        long roundedTotal = Math.round(total);

        Label totalLabel = new Label("總計: $" + roundedTotal);
        totalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // 確認按鈕
        Button confirmButton = new Button("確認");
        confirmButton.setStyle("-fx-font-size: 14px; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        confirmButton.setOnAction(e -> {
            // 確認功能的實現
            System.out.println("訂單確認");
            checkoutStage.close(); // 確認後關閉視窗
        });

        HBox buttonBox = new HBox(confirmButton);
        buttonBox.setAlignment(Pos.CENTER);

        // 將時間、帳號、名稱、TableView、總計Label和確認按鈕加入根容器
        root.getChildren().addAll(headerBox, checkoutTable, totalLabel, buttonBox);

        // 創建Scene並顯示新視窗
        Scene scene = new Scene(root, 500, 400);
        checkoutStage.setScene(scene);
        checkoutStage.show();
    }


    // 初始化所有表格相關的元件與事件
    public void initializeOrderTable() {

        // 訂單陣列串列初始化FXCollections類別有很多靜態方法可以操作ObservableList的"訂單陣列串列"
        order_list = FXCollections.observableArrayList();
        // 前面是空的串列，若已知有兩筆訂單可以這樣設定
        /*
         * order_list = FXCollections.observableArrayList(
         * new Order("p-101", "葡萄汁", 80, 3),
         * new Order("p-102", "番茄汁", 70, 1)
         * );
         */
        // 也可以這樣加入一筆訂單
        // order_list.add(new Order("p-103", "西瓜汁", 80, 3));
        // 表格初始化
        table = new TableView<>();
        table.setEditable(true); // 表格允許修改
        table.setPrefHeight(300);
        table.setPrefWidth(400);
        // 表格內置放的資料來自於order_list，依據置放順序顯示
        table.setItems(order_list);
        table.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");

        // table也可以這樣放入訂單
        // table.getItems().add(new Order("p-104", "奇異果汁", 50, 2));
        // 定義第一個欄位column"品名"，其值來自於Order物件的某個String變數
        TableColumn<OrderDetail, String> order_item_name = new TableColumn("品名");
        // 置放哪個變數值?指定這個欄位 對應到Order的"name"實例變數值
        order_item_name.setCellValueFactory(new PropertyValueFactory("product_name"));
        order_item_name.setPrefWidth(200); // 設定欄位寬度

        // 定義第二個欄位column"價格"，其值來自於OrderDetail物件的product_price變數
        TableColumn<OrderDetail, Double> order_item_price = new TableColumn("價格/kg");
        order_item_price.setCellValueFactory(new PropertyValueFactory("product_price"));
        order_item_price.setPrefWidth(100); // 設定欄位寬度

        // 定義第三個欄位column"數量"，其值來自於OrderDetail物件的quantity變數
        TableColumn<OrderDetail, Integer> order_item_qty = new TableColumn("數量");
        order_item_qty.setCellValueFactory(new PropertyValueFactory("quantity"));
        // 這個欄位值內容可以被修改，因為quantity是整數，因此須將整數轉為字串，才能異動OrderDetail物件，否則會報錯!
        order_item_qty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        order_item_qty.setPrefWidth(100); // 設定欄位寬度

        // 把3個欄位加入table中
        table.getColumns().addAll(order_item_name, order_item_price, order_item_qty);

        // 表格最後一欄是空白，不要顯示!
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 定義數量欄位被修改後進行那些動作
        order_item_qty.setOnEditCommit(new EventHandler<CellEditEvent<OrderDetail, Integer>>() {
            @Override
            public void handle(CellEditEvent<OrderDetail, Integer> event) {

                int row_num = event.getTablePosition().getRow();// 哪一筆被修改
                int new_val = event.getNewValue(); // 改成甚麼數值
                OrderDetail target = event.getTableView().getItems().get(row_num); // 取得該筆果汁傳參考呼叫
                // 修改成新的數值 該筆訂單存放於order_list
                target.setQuantity(new_val);
                // 更新總價
                checkTotal();
                System.out.println(order_list.get(row_num).getQuantity()); // 顯示修改後的數值
            }
        });
    } // initializeOrderTable()

    @Override
    public void start(Stage stage) throws Exception {

        // 根容器 所有的元件都放在裡面container，最後再放進布景中scene，布景再放進舞台中stage
        HBox root = new HBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setStyle("-fx-background-color: #e0e0e0;");
        root.getStylesheets().add("/css/bootstrap3.css");

        VBox leftPane = new VBox();
        leftPane.setSpacing(10);
        leftPane.setPadding(new Insets(10)); // 增加內邊距
//        leftPane.setStyle("-fx-background-color: #bcffad; -fx-border-color: #cccccc;");
        leftPane.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");
        leftPane.setPrefWidth(600); // 設置左邊容器的寬度，確保圖片能夠一行顯示4個

        // 塞入菜單選擇區塊
        leftPane.getChildren().add(getMenuSelectionContainer());

        // 使用 ScrollPane 包裝 menuContainerPane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(menuContainerPane);
        scrollPane.setFitToWidth(true); // 自動調整寬度以適應內容
        scrollPane.setFitToHeight(true); // 自動調整高度以適應內容
        
        // 取得菜單磁磚窗格並放入左邊容器
        leftPane.getChildren().add(scrollPane);

        VBox rightPane = new VBox();
        rightPane.setSpacing(10);
        rightPane.setPadding(new Insets(10)); // 增加內邊距
//        rightPane.setStyle("-fx-background-color: #bcffad; -fx-border-color: #cccccc;");
        rightPane.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");
        rightPane.setPrefWidth(400); // 設置右邊容器的寬度

        // 塞入增加表格刪除項目操作之容器
        rightPane.getChildren().add(getOrderOperationContainer());

        // 塞入表格
        initializeOrderTable(); // 表格要初始化
        rightPane.getChildren().add(table);

        // 塞入白板display
        display.setPrefColumnCount(10);
        display.setStyle("-fx-background-color: #ffffff; -fx-border-color: #cccccc;");
        display.setEditable(false);
        rightPane.getChildren().add(display);

        // 將左邊和右邊容器加入根容器
        root.getChildren().addAll(leftPane, rightPane);
        root.setStyle("-fx-background-color: #15af82;");

        // 塞入布景
        Scene scene = new Scene(root, 900, 550);
        stage.setTitle("蔬果行");
        stage.setScene(scene);
        stage.show();
    } // start()

    public static void main(String[] args) {
        launch(args);
    }

}
