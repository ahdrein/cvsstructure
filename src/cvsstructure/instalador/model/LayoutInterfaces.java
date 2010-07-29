package cvsstructure.instalador.model;

/**
 *
 * @author andrein
 */
public class LayoutInterfaces {
    private Long ordem;
    private String descInterface;
    private String objeto;
    
    public LayoutInterfaces(){
        
    }

    public LayoutInterfaces(Long ordem, String descInterface, String objeto){
        this.ordem = ordem;
        this.descInterface = descInterface;
        this.objeto = objeto;
    }

    /**
     * @return the ordem
     */
    public Long getOrdem() {
        return ordem;
    }

    /**
     * @param ordem the ordem to set
     */
    public void setOrdem(Long ordem) {
        this.ordem = ordem;
    }

    /**
     * @return the descInterface
     */
    public String getDescInterface() {
        return descInterface;
    }

    /**
     * @param descInterface the descInterface to set
     */
    public void setDescInterface(String descInterface) {
        this.descInterface = descInterface;
    }

    /**
     * @return the objeto
     */
    public String getObjeto() {
        return objeto;
    }

    /**
     * @param objeto the objeto to set
     */
    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }


}
