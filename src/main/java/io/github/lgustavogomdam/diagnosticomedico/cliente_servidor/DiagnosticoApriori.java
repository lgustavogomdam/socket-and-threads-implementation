/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package io.github.lgustavogomdam.diagnosticomedico.cliente_servidor;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import io.github.lgustavogomdam.diagnosticomedico.sintomas_doencas.Doenca;
import io.github.lgustavogomdam.diagnosticomedico.sintomas_doencas.Sintoma;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Map.Entry;

public class DiagnosticoApriori {
    private Multimap<Doenca, Sintoma> dadosTreinamento;

    public DiagnosticoApriori() {
        dadosTreinamento = ArrayListMultimap.create();
    }

    public void adicionarExemploTreinamento(Doenca doenca, List<Sintoma> sintomas) {
        dadosTreinamento.putAll(doenca, sintomas);
    }

    public Map<Set<Sintoma>, Integer> treinar(double minSupport) {
        Map<Set<Sintoma>, Integer> itemsetsFrequentes = new HashMap<>();

        // Passo 1: Encontrando os itemsets frequentes de tamanho 1 (sintomas frequentes)
        Map<Sintoma, Integer> frequencias = encontrarItemsetsFrequentesDeTamanho1(minSupport);

        // Passo 2: Gerando itemsets frequentes de tamanho 2 (associações de dois sintomas)
        itemsetsFrequentes.putAll(encontrarItemsetsFrequentesDeTamanho2(frequencias, minSupport));

        return itemsetsFrequentes;
    }

    private Map<Sintoma, Integer> encontrarItemsetsFrequentesDeTamanho1(double minSupport) {
        Map<Sintoma, Integer> frequencias = new HashMap<>();
        for (Sintoma sintoma : dadosTreinamento.values()) {
            frequencias.put(sintoma, frequencias.getOrDefault(sintoma, 0) + 1);
        }

        Map<Sintoma, Integer> itemsetsFrequentes = new HashMap<>();
        for (Entry<Sintoma, Integer> entry : frequencias.entrySet()) {
            if (entry.getValue() >= minSupport) {
                itemsetsFrequentes.put(entry.getKey(), entry.getValue());
            }
        }

        return itemsetsFrequentes;
    }

    private Map<Set<Sintoma>, Integer> encontrarItemsetsFrequentesDeTamanho2(Map<Sintoma, Integer> itemsetsFrequentes, double minSupport) {
        Map<Set<Sintoma>, Integer> itemsetsFrequentesTamanho2 = new HashMap();

        for (Sintoma item1 : itemsetsFrequentes.keySet()) {
            for (Sintoma item2 : itemsetsFrequentes.keySet()) {
                if (item1 != item2) {
                    Set<Sintoma> itemset = new HashSet<>();
                    itemset.add(item1);
                    itemset.add(item2);

                    int support = calcularSuporte(itemset);
                    if (support >= minSupport) {
                        itemsetsFrequentesTamanho2.put(itemset, support);
                    }
                }
            }
        }

        return itemsetsFrequentesTamanho2;
    }

    private int calcularSuporte(Set<Sintoma> itemset) {
        int suporte = 0;
        for (Collection<Sintoma> sintomas : dadosTreinamento.asMap().values()) {
            if (sintomas.containsAll(itemset)) {
                suporte++;
            }
        }
        return suporte;
    }
    
    
    public String diagnosticar(List<Sintoma> sintomas, double minSupport) {
        Map<Set<Sintoma>, Integer> itemsetsFrequentes = treinar(minSupport);
        Map<Doenca, Integer> contagemDoencas = new HashMap<>();

        for (Doenca doenca : dadosTreinamento.keySet()) {
            for (Set<Sintoma> itemset : itemsetsFrequentes.keySet()) {
                if (dadosTreinamento.get(doenca).containsAll(itemset) && sintomas.containsAll(itemset)) {
                    contagemDoencas.put(doenca, contagemDoencas.getOrDefault(doenca, 0) + 1);
                }
            }
        }

        if (contagemDoencas.isEmpty()) {
            // Retorna um possível diagnóstico (primeira doença encontrada) e informa que não foi encontrado um diagnóstico confiável
            String possivelDiagnostico = dadosTreinamento.keySet().iterator().next().toString();
            
            return "Aviso: Não foi encontrado um diagnóstico confiável.\nPossível diagnóstico: " + possivelDiagnostico;
        } else {
            return Collections.max(contagemDoencas.entrySet(), Entry.comparingByValue()).getKey().toString();
        }
    }

    public static void imprimirDiagnostico(Map<String, Double> diagnósticos) {
        for (Map.Entry<String, Double> entry : diagnósticos.entrySet()) {
            String doenca = entry.getKey();
            double confianca = entry.getValue();
            System.out.println("Doença: " + doenca);
            System.out.println("Confiança de Diagnóstico: " + confianca);
        }
    }

}

