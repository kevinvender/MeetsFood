package com.sidera.meetsfood.api.beans;

/**
 * Created by kevin.vender on 20/09/2017.
 */
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContabilitaRowV20  extends ContabilitaRow
{
    public String flag_giorno;

    public ContabilitaRowV20(ResultSet resultSet) throws SQLException {
        super(resultSet);
        flag_giorno = resultSet.getString("FLAG_GIORNO");
    }
}
