package commun.pojo;

public class Passageiro {
    //Definição básicas sobre um passageiro
    //private static final long serialVersionUID = 1L; // por enquanto n vou usar
    private String nome;                                
    private String cpf;                                 //Futuramente fazer um tratamento sobre esse cpf                     
    private String dataNascimento;                      //formato : DD/MM/YYYY
    //Por enquanto irei colocar somente esses atributos, passivo de alteração

    //Constructor
    public Passageiro (String nome, String cpf, String dataNascimento){
        this.nome = nome;
        this.cpf = cpf;
        this.dataNascimento = dataNascimento;
    }

    //Getters
    public String getNome() { return nome; }
    public String getCpf() { return cpf; }
    public String getDataNascimento() { return dataNascimento; }
}
