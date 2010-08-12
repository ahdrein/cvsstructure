/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cvsstructure.model;

/**
 *
 * @author andrein
 */
public class Cliente {

    public static String userNameSys = "";
    private Usuario ioUser;
    private Usuario itUser;
    private Usuario bgUser;
    private Usuario bsUser;
    private Usuario isUser;
    private Usuario ceUser;
    private Usuario ciUser;
    private Usuario exUser;
    private Usuario dbUser;
    private Usuario caiUser;
    private Usuario appsUser;
    private String dbLinkCai;
    private String dbLinkApps;
    private String conn;
    private String dataBase;
    private String tns;
    private String port;
    private String service;
    private static Cliente instance;

    static {
        instance = new Cliente();
    }

    public Cliente() {
    }

    public static Cliente getInstance() {
        return instance;
    }

    public static void setInstance(Cliente cliente) {
        instance = cliente;
    }

    /**
     * @return the ioUser
     */
    public Usuario getIoUser() {
        return ioUser;
    }

    /**
     * @param ioUser the ioUser to set
     */
    public void setIoUser(Usuario ioUser) {
        this.ioUser = ioUser;
    }

    /**
     * @return the itUser
     */
    public Usuario getItUser() {
        return itUser;
    }

    /**
     * @param itUser the itUser to set
     */
    public void setItUser(Usuario itUser) {
        this.itUser = itUser;
    }

    /**
     * @return the bgUser
     */
    public Usuario getBgUser() {
        return bgUser;
    }

    /**
     * @param bgUser the bgUser to set
     */
    public void setBgUser(Usuario bgUser) {
        this.bgUser = bgUser;
    }

    /**
     * @return the bsUser
     */
    public Usuario getBsUser() {
        return bsUser;
    }

    /**
     * @param bsUser the bsUser to set
     */
    public void setBsUser(Usuario bsUser) {
        this.bsUser = bsUser;
    }

    /**
     * @return the isUser
     */
    public Usuario getIsUser() {
        return isUser;
    }

    /**
     * @param isUser the isUser to set
     */
    public void setIsUser(Usuario isUser) {
        this.isUser = isUser;
    }

    /**
     * @return the ceUser
     */
    public Usuario getCeUser() {
        return ceUser;
    }

    /**
     * @param ceUser the ceUser to set
     */
    public void setCeUser(Usuario ceUser) {
        this.ceUser = ceUser;
    }

    /**
     * @return the ciUser
     */
    public Usuario getCiUser() {
        return ciUser;
    }

    /**
     * @param ciUser the ciUser to set
     */
    public void setCiUser(Usuario ciUser) {
        this.ciUser = ciUser;
    }

    /**
     * @return the exUser
     */
    public Usuario getExUser() {
        return exUser;
    }

    /**
     * @param exUser the exUser to set
     */
    public void setExUser(Usuario exUser) {
        this.exUser = exUser;
    }

    /**
     * @return the dbUser
     */
    public Usuario getDbUser() {
        return dbUser;
    }

    /**
     * @param dbUser the dbUser to set
     */
    public void setDbUser(Usuario dbUser) {
        this.dbUser = dbUser;
    }

    /**
     * @return the caiUser
     */
    public Usuario getCaiUser() {
        return caiUser;
    }

    /**
     * @param caiUser the caiUser to set
     */
    public void setCaiUser(Usuario caiUser) {
        this.caiUser = caiUser;
    }

    /**
     * @return the appsUser
     */
    public Usuario getAppsUser() {
        return appsUser;
    }

    /**
     * @param appsUser the appsUser to set
     */
    public void setAppsUser(Usuario appsUser) {
        this.appsUser = appsUser;
    }

    /**
     * @return the dbLinkCai
     */
    public String getDbLinkCai() {
        return dbLinkCai;
    }

    /**
     * @param dbLinkCai the dbLinkCai to set
     */
    public void setDbLinkCai(String dbLinkCai) {
        this.dbLinkCai = dbLinkCai;
    }

    /**
     * @return the dbLinkApps
     */
    public String getDbLinkApps() {
        return dbLinkApps;
    }

    /**
     * @param dbLinkApps the dbLinkApps to set
     */
    public void setDbLinkApps(String dbLinkApps) {
        this.dbLinkApps = dbLinkApps;
    }

    /**
     * @return the conn
     */
    public String getConn() {
        return conn;
    }

    /**
     * @param conn the conn to set
     */
    public void setConn(String conn) {
        this.conn = conn;
    }

    /**
     * @return the dataBase
     */
    public String getDataBase() {
        return dataBase;
    }

    /**
     * @param dataBase the dataBase to set
     */
    public void setDataBase(String dataBase) {
        this.dataBase = dataBase;
    }

    /**
     * @return the tns
     */
    public String getTns() {
        return tns;
    }

    /**
     * @param tns the tns to set
     */
    public void setTns(String tns) {
        this.tns = tns;
    }

    /**
     * @return the port
     */
    public String getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * @return the service
     */
    public String getService() {
        return service;
    }

    /**
     * @param service the service to set
     */
    public void setService(String service) {
        this.service = service;
    }
}
