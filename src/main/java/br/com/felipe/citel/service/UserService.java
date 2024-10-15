package br.com.felipe.citel.service;

import br.com.felipe.citel.model.User;
import br.com.felipe.citel.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User findByid(Long id){
        Optional<User> searchUserById = userRepository.findById(id);
        if(searchUserById.isEmpty()){
            return null;
        }
        return searchUserById.get();
    }

    public User create(User userRequest) {


        if(userRepository.existsByCpf(userRequest.getCpf())){
            log.error("CPF já cadastrado");
            throw new RuntimeException("CPF já cadastrado");
        }

        if (userRepository.existsByEmail(userRequest.getEmail())){
            log.error("Email já cadastrado");
            throw new RuntimeException("Email já cadastrado");
        }

        return userRepository.save(userRequest);
    }

    private  boolean hasAnyUserCreated(User usuario) {

        boolean hasAnyCpfEquals = userRepository.findAll()
                .stream()
                .filter(userAtual -> userAtual.getCpf().equals(usuario.getCpf()))
                .toList()
                .isEmpty();

        boolean hasAnyEmailEquals = userRepository.findAll()
                .stream()
                .filter(userAtual -> userAtual.getEmail().equals(usuario.getEmail()))
                .toList()
                .isEmpty();

        return hasAnyCpfEquals || hasAnyEmailEquals;
    }

    public Map<String, Long> getCandidatosPorEstado(List<User> allUsers) {
        return allUsers.stream()
                .collect(Collectors.groupingBy(User::getEstado, Collectors.counting()));
    }

    public Map<String, Double> getImcMedioPorFaixaEtaria(List<User> allUsers) {
        Map<String, List<Double>> imcPorFaixaEtaria = new HashMap<>();

        for (User user : allUsers) {
            int idade = calcularIdade(user.getDataNasc());
            String faixaEtaria = calcularFaixaEtaria(idade);
            double imc = calcularImc(user.getPeso(), user.getAltura());

            imcPorFaixaEtaria.computeIfAbsent(faixaEtaria, k -> new ArrayList<>()).add(imc);
        }

        return imcPorFaixaEtaria.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            double imcMedio = entry.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0);
                            return Math.round(imcMedio * 100.0) / 100.0;
                        }));
    }


    public Map<String, Double> getPercentualObesos() {
        List<User> allUsers = userRepository.findAll();
        long totalHomens = allUsers.stream().filter(u -> u.getSexo().equalsIgnoreCase("masculino")).count();
        long totalMulheres = allUsers.stream().filter(u -> u.getSexo().equalsIgnoreCase("feminino")).count();

        long obesosHomens = allUsers.stream()
                .filter(u -> u.getSexo().equalsIgnoreCase("masculino"))
                .filter(u -> calcularImc(u.getPeso(), u.getAltura()) > 30).count();

        long obesosMulheres = allUsers.stream()
                .filter(u -> u.getSexo().equalsIgnoreCase("feminino"))
                .filter(u -> calcularImc(u.getPeso(), u.getAltura()) > 30).count();

        double percentualHomens = (totalHomens == 0) ? 0 : (double) obesosHomens / totalHomens * 100;
        double percentualMulheres = (totalMulheres == 0) ? 0 : (double) obesosMulheres / totalMulheres * 100;

        percentualHomens = Math.round(percentualHomens * 100.0) / 100.0;
        percentualMulheres = Math.round(percentualMulheres * 100.0) / 100.0;

        Map<String, Double> percentualObesos = new HashMap<>();
        percentualObesos.put("masculino", percentualHomens);
        percentualObesos.put("feminino", percentualMulheres);

        return percentualObesos;
    }


    public Map<String, Double> getMediaIdadePorTipoSanguineo() {
        List<User> allUsers = userRepository.findAll();
        Map<String, List<Integer>> idadesPorTipoSanguineo = new HashMap<>();

        for (User user : allUsers) {
            String tipoSanguineo = user.getTipoSanguineo();
            int idade = calcularIdade(user.getDataNasc());

            idadesPorTipoSanguineo.computeIfAbsent(tipoSanguineo, k -> new ArrayList<>()).add(idade);
        }

        return idadesPorTipoSanguineo.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> {
                            double mediaIdade = entry.getValue().stream().mapToInt(Integer::intValue).average().orElse(0);
                            return Math.round(mediaIdade * 100.0) / 100.0;
                        }));
    }


    public Map<String, Long> getPossiveisDoadores() {
        List<User> allUsers = userRepository.findAll(); // Buscar todos os usuários do repositório
        Map<String, Long> doadoresPorTipo = new HashMap<>();

        for (User user : allUsers) {
            if (podeDoar(user)) {
                for (String tipoReceptor : getTiposSanguineosReceptor(user.getTipoSanguineo())) {
                    doadoresPorTipo.put(tipoReceptor, doadoresPorTipo.getOrDefault(tipoReceptor, 0L) + 1);
                }
            }
        }

        return doadoresPorTipo;
    }


    private int calcularIdade(String dataNasc) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate nascimento = LocalDate.parse(dataNasc, formatter);
            return Period.between(nascimento, LocalDate.now()).getYears();
        } catch (DateTimeParseException e) {
            log.error("Erro ao parsear data: " + dataNasc);
            throw new RuntimeException("Formato de data inválido: " + dataNasc);
        }
    }

    private String calcularFaixaEtaria(int idade) {
        if (idade <= 10) return "0-10";
        if (idade <= 20) return "11-20";
        if (idade <= 30) return "21-30";
        if (idade <= 40) return "31-40";
        if (idade <= 50) return "41-50";
        if (idade <= 60) return "51-60";
        return "61+";
    }

    private double calcularImc(double peso, double altura) {
        return peso / (altura * altura);
    }

    private boolean podeDoar(User user) {
        int idade = calcularIdade(user.getDataNasc());
        return idade >= 16 && idade <= 69 && user.getPeso() > 50;
    }

    private List<String> getTiposSanguineosReceptor(String tipoSanguineo) {
        switch (tipoSanguineo) {
            case "A+":
                return List.of("AB+", "A+");
            case "A-":
                return List.of("A+", "A-", "AB+", "AB-");
            case "B+":
                return List.of("B+", "AB+");
            case "B-":
                return List.of("B+", "B-", "AB+", "AB-");
            case "AB+":
                return List.of("AB+");
            case "AB-":
                return List.of("AB+", "AB-");
            case "O+":
                return List.of("A+", "B+", "O+", "AB+");
            case "O-":
                return List.of("A+", "B+", "O+", "AB+", "A-", "B-", "O-", "AB-");
            default:
                return List.of();
        }
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }



}