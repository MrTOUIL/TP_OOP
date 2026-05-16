public class Poisson {
    private String espece;
    private ProgrammeAlimentaire pg;
    public Poisson() {
    }
    public Poisson(String espece, ProgrammeAlimentaire pg) {
        this.espece = espece;
        this.pg = pg;
    }
    public String getEspece() {
        return espece;
    }
    public void setEspece(String espece) {
        this.espece = espece;
    }
    public ProgrammeAlimentaire getPg() {
        return pg;
    }
    public void setPg(ProgrammeAlimentaire pg) {
        this.pg = pg;
    }
}
