module be.esi.prj.bookle {
    requires javafx.controls;
    requires javafx.fxml;
    requires tess4j;
    requires com.google.gson;
    requires java.sql;


    opens be.esi.prj.bookle.mvp.view to javafx.fxml;
    exports be.esi.prj.bookle.mvp;
    exports be.esi.prj.bookle.mvp.model;
    opens be.esi.prj.bookle.mvp.presenter to javafx.fxml;
    exports be.esi.prj.bookle.mvp.model.dataBaseRepository;
    exports be.esi.prj.bookle.mvp.model.dataAccessObject;
    exports be.esi.prj.bookle.mvp.view;
    exports be.esi.prj.bookle.mvp.model.dto;
    exports be.esi.prj.bookle.observer;
    exports be.esi.prj.bookle.mvp.presenter;
    exports be.esi.prj.bookle.mvp.model.scanner;
}