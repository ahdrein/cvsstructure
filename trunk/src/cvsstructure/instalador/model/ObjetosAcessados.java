package cvsstructure.instalador.model;

/**
 *
 * @author andrein
 */
public class ObjetosAcessados {
    private String objeto;
    private String objetoAcessado;

    public void ObjectsAcessados(){
    }

    public void ObjectsAcessados(String objeto, String objetoAcessado){
        this.objeto = objeto;
        this.objetoAcessado = objetoAcessado;
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

    /**
     * @return the objetoAcessado
     */
    public String getObjetoAcessado() {
        return objetoAcessado;
    }

    /**
     * @param objetoAcessado the objetoAcessado to set
     */
    public void setObjetoAcessado(String objetoAcessado) {
        this.objetoAcessado = objetoAcessado;
    }
}
