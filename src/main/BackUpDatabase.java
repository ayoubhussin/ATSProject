package main;

import javafx.scene.control.Alert;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BackUpDatabase {

    public BackUpDatabase() {
    }

    public void LoadBackup(String tableName, String backupName) throws SQLException {
        DatabaseConection db = new DatabaseConection();
        db.AdminConnection();

        try {
            db.executeStatement(String.format("DELETE FROM %s",tableName ));
            db.executeStatement(String.format("INSERT INTO %s SELECT * FROM %s", tableName, backupName));

            Alert good = new Alert(Alert.AlertType.INFORMATION);
            good.setHeaderText("Success");
            good.setContentText("Backup load was successful");
            good.showAndWait();

        } catch (SQLException SQLE) {
            Alert erorr = new Alert(Alert.AlertType.ERROR);
            erorr.setHeaderText("Error");
            erorr.setContentText("Unable to load backup");
            erorr.showAndWait();
        }

        db.CloseConnection();
    }

    public void CreateBackup(String tableName) throws SQLException{
        DatabaseConection db = new DatabaseConection();
        db.AdminConnection();

        try {
            DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy_MM_dd");
            LocalDate date = LocalDate.now();
            String date2 = DATE_FORMATTER.format(date);

            String stm = String.format("CREATE TABLE %s_backup_%s LIKE %s", tableName, date2, tableName);

            db.executeStatement(stm);
            db.executeStatement(String.format("INSERT INTO %s_backup_%s SELECT * FROM %s", tableName, date2, tableName));

            db.executeStatement(String.format("INSERT INTO Backups (backupName, date, tableBackup) VALUES ('%s_backup_%s','%s','%s')", tableName, date2, date, tableName));

            Alert erorr = new Alert(Alert.AlertType.INFORMATION);
            erorr.setHeaderText("Success");
            erorr.setContentText("Backup made in the database: "+tableName+"_backup_"+date2);
            erorr.showAndWait();

        } catch (SQLException SQLE) {
            Alert erorr = new Alert(Alert.AlertType.ERROR);
            erorr.setHeaderText("Error");
            erorr.setContentText("UNABLE TO MAKE BACKUP - Backup already exists");
            erorr.showAndWait();
        }

        db.CloseConnection();
    }

}

