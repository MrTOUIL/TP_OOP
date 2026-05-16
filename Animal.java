import java.util.UUID;
public class Animal {
    private ProgrammeAlimentaire pg;
    private UUID id = UUID.randomUUID();
    private espece gen;
    private double age;
    private double poids;
    private etatdesante sante;
    private Biometrique laisse;
    public Animal() {
    }
    public Animal(ProgrammeAlimentaire pg, espece gen, double age, double poids, etatdesante sante, Biometrique laisse) {
        this.pg = pg;
        this.gen = gen;
        this.age = age;
        this.poids = poids;
        this.sante = sante;
        this.laisse = laisse;
    }
    public ProgrammeAlimentaire getPg() {
        return pg;
    }
    public void setPg(ProgrammeAlimentaire pg) {
        this.pg = pg;
    }
    public UUID getId() {
        return id;
    }
    public espece getGen() {
        return gen;
    }
    public void setGen(espece gen) {
        this.gen = gen;
    }
    public double getAge() {
        return age;
    }
    public void setAge(double age) {
        this.age = age;
    }
    public double getPoids() {
        return poids;
    }
    public void setPoids(double poids) {
        this.poids = poids;
    }
    public etatdesante getSante() {
        return sante;
    }
    public void setSante(etatdesante sante) {
        this.sante = sante;
    }
    public Biometrique getLaisse() {
        return laisse;
    }
    public void setLaisse(Biometrique laisse) {
        this.laisse = laisse;
    }
}
