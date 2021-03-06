package modell;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;



public class AdatBazisKezeles implements AdatbazisKapcsolat {
  private static Connection kapcsolat;

  private static void kapcsolatNyit() {
    try {
      Class.forName(DRIVER);
      kapcsolat = DriverManager.getConnection(URL, USER, PASSWORD);
    }
    catch (ClassNotFoundException e) {
      System.out.println("Hiba! Hiányzik a JDBC driver.");
    }
    catch (SQLException e) {
      System.out.println("Hiba! Nem sikerült megnyitni a kapcsolatot az adatbázis-szerverrel.");
    }
  }

  private static void kapcsolatZar() {
    try {
      kapcsolat.close();
    }
    catch (SQLException e) {
      System.out.println("Hiba! Nem sikerült lezárni a kapcsolatot az adatbázis-szerverrel.");
    }
  }
  
  //TODO szukseges lekerdezesek es ezek visszaadasa
  
  public ArrayList<String> lekerdezOsszesDolgozoListaja(String reszleg) { //Osszes dolgozo
    ArrayList<String> lista=new ArrayList<>();
    try {
      kapcsolatNyit();
      Statement s = kapcsolat.createStatement();
      ResultSet rs = s.executeQuery(
        "SELECT FIRST_NAME || ' ' || LAST_NAME AS NÉV "+
        "FROM EMPLOYEES E\n" +
        "ORDER BY NÉV");
      while(rs.next())
        lista.add(rs.getString("NÉV"));
      kapcsolatZar();
    }
    catch(SQLException e) {
      System.out.println(e.getMessage());
    }
    return lista;    
  }
  
  public ArrayList<String> lekerdezDolgozokListajaAdottReszleghez(String reszleg) { //Adott reszleg dolgozoi
    ArrayList<String> lista=new ArrayList<>();
    try {
      kapcsolatNyit();
      PreparedStatement ps=kapcsolat.prepareStatement(
        "SELECT FIRST_NAME || ' ' || LAST_NAME AS NÉV "+
        "FROM DEPARTMENTS D, EMPLOYEES E\n" +
        "WHERE D.DEPARTMENT_ID=E.DEPARTMENT_ID AND D.DEPARTMENT_NAME=?"+
        "ORDER BY NÉV");
      ps.setString(1, reszleg);
      ResultSet rs=ps.executeQuery();
      while(rs.next())
        lista.add(rs.getString("NÉV"));
      kapcsolatZar();
    }
    catch(SQLException e) {
      System.out.println(e.getMessage());
    }
    return lista;    
  }
  
  
  
  public int[] lekerdezMinMaxFizetes(int munkakorAzonosito) { //Adott munkakorhoz tartozo min es max fizetes
    int[] minmaxFizetes={0,0};
    try {
      kapcsolatNyit();
      PreparedStatement ps=kapcsolat.prepareStatement(
        "SELECT MIN_SALARY AS MINFIZETÉS, MAX_SALARY AS MAXFIZETÉS \n" +
        "FROM JOBS\n" +
        "WHERE JOB_ID=?");
      ps.setString(1, ""+munkakorAzonosito);
      ResultSet rs=ps.executeQuery();        
      rs.next();
      minmaxFizetes[0]=rs.getInt("MINFIZETÉS");
      minmaxFizetes[1]=rs.getInt("MAXFIZETÉS");
      kapcsolatZar();
    }
    catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return minmaxFizetes;
  }
    
  public int lekerdezMinFizetes(int munkakorAzonosito) { //Adott munkakorhoz tartozo min fizetes
    int fizetes=0;
    try {
      kapcsolatNyit();
      PreparedStatement ps=kapcsolat.prepareStatement(
        "SELECT MIN_SALARY AS MINFIZETÉS \n" +
        "FROM JOBS\n" +
        "WHERE JOB_ID=?");
      ps.setString(1, ""+munkakorAzonosito);
      ResultSet rs=ps.executeQuery();        
      rs.next();
      fizetes=rs.getInt("MINFIZETÉS");
      kapcsolatZar();
    }
    catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return fizetes;
  }

  public int lekerdezMaxFizetés(int munkakorAzonosito) { //Adott munkakorhoz tartozo max fizetes
    int fizetes=0;
    try {
      kapcsolatNyit();
      PreparedStatement ps=kapcsolat.prepareStatement(
        "SELECT MAX_SALARY AS MAXFIZETÉS \n" +
        "FROM JOBS\n" +
        "WHERE JOB_ID=?");
      ps.setString(1, ""+munkakorAzonosito);
      ResultSet rs=ps.executeQuery();        
      rs.next();
      fizetes=rs.getInt("MAXFIZETÉS");
      kapcsolatZar();
    }
    catch (SQLException e) {
      System.out.println(e.getMessage());
    }
    return fizetes;
  }

/* Egyelőre ez a két lekérdezés van használataban ....  */  
  
  public ArrayList<Dolgozo> lekerdezDolgozokListajaAdottReszleghez(int reszlegId) { //Adott reszleg dolgozoi
    ArrayList<Dolgozo> lista=new ArrayList<>();
    try {
      kapcsolatNyit();
      PreparedStatement ps;
      if(reszlegId==-1){
      ps=kapcsolat.prepareStatement(
              "SELECT EMP_DETAILS_VIEW.EMPLOYEE_ID as empId,\n" +
              "EMP_DETAILS_VIEW.FIRST_NAME || ' ' || EMP_DETAILS_VIEW.LAST_NAME as name,\n" +
              "EMP_DETAILS_VIEW.DEPARTMENT_ID as depId,\n" +
              "EMP_DETAILS_VIEW.DEPARTMENT_NAME as depName,\n" +
              "EMP_DETAILS_VIEW.JOB_TITLE as jobTitle,\n" +
              "EMP_DETAILS_VIEW.SALARY as SALARY,\n" +
              "JOBS.MIN_SALARY as MIN_SALARY,\n" +
              "JOBS.MAX_SALARY as MAX_SALARY \n" +
              "FROM JOBS JOBS,\n" +
              "EMP_DETAILS_VIEW EMP_DETAILS_VIEW\n" +
              "WHERE EMP_DETAILS_VIEW.JOB_ID=JOBS.JOB_ID "+
              "ORDER BY NAME");
      //ps.setString(1, ""+reszlegId);
      }else{
      ps=kapcsolat.prepareStatement(
              "SELECT EMP_DETAILS_VIEW.EMPLOYEE_ID as empId,\n" +
              "EMP_DETAILS_VIEW.FIRST_NAME || ' ' || EMP_DETAILS_VIEW.LAST_NAME as name,\n" +
              "EMP_DETAILS_VIEW.DEPARTMENT_ID as depId,\n" +
              "EMP_DETAILS_VIEW.DEPARTMENT_NAME as depName,\n" +
              "EMP_DETAILS_VIEW.JOB_TITLE as jobTitle,\n" +
              "EMP_DETAILS_VIEW.SALARY as SALARY,\n" +
              "JOBS.MIN_SALARY as MIN_SALARY,\n" +
              "JOBS.MAX_SALARY as MAX_SALARY \n" +
              "FROM JOBS JOBS,\n" +
              "EMP_DETAILS_VIEW EMP_DETAILS_VIEW\n" +
              "WHERE EMP_DETAILS_VIEW.JOB_ID=JOBS.JOB_ID "+
              "AND EMP_DETAILS_VIEW.DEPARTMENT_ID=?\n" +
              "ORDER BY NAME");
      ps.setString(1, ""+reszlegId);
      }
      ResultSet rs=ps.executeQuery();
      while(rs.next()){
        Dolgozo dolgozo = new Dolgozo(rs.getInt("empId"), 
                                      rs.getString("name"), 
                                      rs.getInt("depId"), 
                                      rs.getString("depName"), 
                                      rs.getString("jobTitle"), 
                                      rs.getInt("SALARY"), 
                                      rs.getInt("MIN_SALARY"), 
                                      rs.getInt("MAX_SALARY"));
        lista.add(dolgozo);
      }
      kapcsolatZar();
    }
    catch(SQLException e) {
      System.out.println(e.getMessage());
    }
    return lista;    
  }
  
  public ArrayList<Reszleg> lekerdezReszleg() {
    ArrayList<Reszleg> lista = new ArrayList<>();
    try {
      kapcsolatNyit();
      Statement s = kapcsolat.createStatement();
      ResultSet rs = s.executeQuery(
              "SELECT DEPARTMENT_ID, DEPARTMENT_NAME\n" +
              "FROM DEPARTMENTS\n" +
              "WHERE DEPARTMENT_ID IN \n" +
              "(SELECT DISTINCT DEPARTMENT_ID FROM EMPLOYEES)\n" +
              "ORDER BY 2");
      
      while (rs.next()){
        Reszleg reszleg = new Reszleg(rs.getString("DEPARTMENT_NAME"), rs.getInt("DEPARTMENT_ID"));
        lista.add(reszleg);
      }
      kapcsolatZar();
    }
    catch (SQLException e) {
      e.printStackTrace();
    }
    return lista;
  }
  
  //protected abstract DefaultListModel faModell();
  
}
