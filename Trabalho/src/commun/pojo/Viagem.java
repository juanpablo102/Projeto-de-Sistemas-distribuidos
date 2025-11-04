package commun.pojo;

public class Viagem {
    private Rota rota;                   // viagem tem uma rota
    private String dataPartida;          // formato: DD/MM/YYYY
    private String horaPartida;          // formato: HH:MM
    private String horaChegada;          // formato: HH:MM
    private int vagasDisponiveis;        
    
    // Construtor
    public Viagem(Rota rota, String dataPartida, String horaPartida,
                  String horaChegada, int vagasDisponiveis) {
        this.rota = rota;
        this.dataPartida = dataPartida;
        this.horaPartida = horaPartida;
        this.horaChegada = horaChegada;
        this.vagasDisponiveis = vagasDisponiveis;
    }
    
    // Getters
    public Rota getRota() { return rota; }
    public String getDataPartida() { return dataPartida; }
    public String getHoraPartida() { return horaPartida; }
    public String getHoraChegada() { return horaChegada; }
    public int getVagasDisponiveis() { return vagasDisponiveis; }
    
    // Setters
    public void setVagasDisponiveis(int vagasDisponiveis) { 
        this.vagasDisponiveis = vagasDisponiveis; 
    }
}
