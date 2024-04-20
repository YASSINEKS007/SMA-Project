module ma.enset.project_sma_final {
    requires javafx.controls;
    requires javafx.fxml;
    requires jade;

    opens ma.enset.project_sma_final to javafx.fxml;
    exports ma.enset.project_sma_final;
    exports ma.enset.project_sma_final.agents;


}