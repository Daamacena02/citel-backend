package br.com.felipe.citel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "CPF é obrigatório")
    private String cpf;

    @NotBlank(message = "RG é obrigatório")
    private String rg;

    @NotNull(message = "Data de Nascimento é obrigatória")
    @JsonProperty("data_nasc")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
    private String dataNasc;

    @NotBlank(message = "Sexo é obrigatório")
    private String sexo;

    @NotBlank(message = "Nome da Mãe é obrigatório")
    private String mae;

    @NotBlank(message = "Nome do Pai é obrigatório")
    private String pai;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    private String email;

    private String cep;

    private String endereco;

    private int numero;

    private String bairro;

    private String cidade;

    private String estado;

    @JsonProperty("telefone_fixo")
    private String telefoneFixo;

    private String celular;

    @NotNull(message = "Altura é obrigatória")
    private double altura;

    @NotNull(message = "Peso é obrigatório")
    private double peso;

    @JsonProperty("tipo_sanguineo")
    private String tipoSanguineo;
}
