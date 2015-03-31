package nl.jk_5.pumpkin.server.sql;

import org.apache.commons.lang3.StringUtils;

import nl.jk_5.pumpkin.server.Pumpkin;
import nl.jk_5.pumpkin.server.util.SqlUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class TableInfo {

    private final String name;

    private final Map<String, String> columns = new HashMap<String, String>();
    private final Set<String> primaryKeys = new HashSet<String>();
    private final Set<String> nullableKeys = new HashSet<String>();

    public TableInfo(String name) {
        this.name = name;
    }

    public String getCreateStatement() {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS \"");
        sb.append(name);
        sb.append("\" (");
        for (Map.Entry<String, String> e : this.columns.entrySet()) {
            sb.append("\"");
            sb.append(e.getKey());
            sb.append("\" ");
            sb.append(e.getValue());
            if(nullableKeys.contains(e.getKey())){
                sb.append(", ");
            }else{
                sb.append(" NOT NULL, ");
            }
        }
        sb.append("PRIMARY KEY (\"");
        sb.append(StringUtils.join(primaryKeys, "\", \""));
        sb.append("\"))");
        return sb.toString();
    }

    public String createSelectStatement(Collection<String> fields) {
        for(String f : fields){
            if(!columns.containsKey(f)){
                throw new RuntimeException("Column " + f + " does not exist in table " + this.name);
            }
        }
        StringBuilder sb = new StringBuilder("SELECT \"");
        sb.append(StringUtils.join(fields, "\", \""));
        sb.append("\" FROM \"");
        sb.append(name);
        sb.append("\"");
        return sb.toString();
    }

    public String createSelectStatement(String[] fields) {
        return createSelectStatement(Arrays.asList(fields));
    }

    public String createSelectStatement() {
        StringBuilder sb = new StringBuilder("SELECT * FROM \"");
        sb.append(name);
        sb.append("\"");
        return sb.toString();
    }

    public String createInsertOrReplace(Map<String, Object> fieldsAndValues) {
        for(String f : fieldsAndValues.keySet()){
            if (!columns.containsKey(f)){
                throw new RuntimeException("Field " + f + " does not exist in table " + this.name);
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append("REPLACE INTO \"");
        sb.append(name);
        sb.append("\" (\"");
        sb.append(StringUtils.join(fieldsAndValues.keySet(), "\", \""));
        sb.append("\") VALUES ('");
        sb.append(StringUtils.join(fieldsAndValues.values(), "', '"));
        sb.append("')");
        return sb.toString();
    }

    public String createTruncate(){
        return "TRUNCATE TABLE \"" + name + "\"";
    }

    public List<Map<String, Object>> loadList() throws SQLException {
        Connection conn = null;
        try{
            conn = Pumpkin.instance().getSqlService().getDataSource().getConnection();
            ResultSet resultSet = conn.createStatement().executeQuery(createSelectStatement());
            ResultSetMetaData meta = resultSet.getMetaData();
            int columnsCount = meta.getColumnCount();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            while(resultSet.next()){
                Map<String, Object> row = new HashMap<String, Object>(columnsCount);
                for(int columnIndex = 1; columnIndex <= columnsCount; ++columnIndex){
                    row.put(meta.getColumnName(columnIndex).toLowerCase(), resultSet.getObject(columnIndex));
                }
                list.add(row);
            }
            return list;
        }finally{
            SqlUtils.close(conn);
        }
    }

    public Map<String, String> loadMap(String key, String value) throws SQLException {
        Connection conn = null;
        try{
            conn = Pumpkin.instance().getSqlService().getDataSource().getConnection();
            List<String> fields = new ArrayList<String>();
            fields.add(key);
            fields.add(value);
            ResultSet resultSet = conn.createStatement().executeQuery(createSelectStatement(fields));
            Map<String, String> result = new HashMap<String, String>();
            while(resultSet.next()){
                result.put(resultSet.getString(key), resultSet.getString(value));
            }
            return result;
        }finally{
            SqlUtils.close(conn);
        }
    }

    public void addColumn(String name, String type){
        this.columns.put(name, type);
    }

    public void addPrimaryKey(String name){
        this.primaryKeys.add(name);
    }

    public void addNullable(String name){
        this.primaryKeys.add(name);
    }

    public String getName() {
        return name;
    }
}
