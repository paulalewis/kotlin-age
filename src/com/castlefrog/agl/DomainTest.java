package com.castlefrog.agl;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.yaml.snakeyaml.Yaml;

/**
 * This class is used to run tests between agents on simulators.
 */
public final class DomainTest {
    private static final String ROOT_ELEMENT = "domainTest";
    private static final String N_SIMULATIONS_ELEMENT = "nSimulations";
    private static final String DOMAIN_ELEMENT = "domain";

    private List<Agent> agents = new ArrayList<>();
    private Simulator<?, ?> domain;
    private int nSimulations;

    /**
     * @param args
     *      test_filename - path to the xml test file
     *      output_filename - optional path to output file
     */
    private DomainTest(String[] args) {
        if (args.length == 1 || args.length == 2) {
            registerSimulators();
            registerAgents();
            try {
                File inputFile = new File(args[0]);
                if (inputFile.getName().contains(".yaml")) {
                    readYamlFile(inputFile);
                } else if (inputFile.getName().contains(".xml")) {
                    readXmlFile(inputFile);
                } else {
                    throw new IllegalArgumentException("Invalid file type: " + args[0]);
                }

                List<Simulator<?, ?>> simulators = new ArrayList<>();
                List<double[]> rewardsData = new ArrayList<>();
                List<double[]> avgMoveTimeData = new ArrayList<>();
                for (int i = 0; i < agents.size(); i += 1) {
                    simulators.add(domain.copy());
                    rewardsData.add(new double[nSimulations]);
                    avgMoveTimeData.add(new double[nSimulations]);
                }
                Arbiter<?, ?> arbiter = new Arbiter(domain.getState(), domain, simulators, agents);
                for (int i = 0; i < nSimulations; i += 1) {
                    arbiter.reset();
                    try {
                        int count = 0;
                        while (!arbiter.isTerminalState()) {
                            arbiter.step();
                            count += 1;
                            for (int j = 0; j < agents.size(); j += 1) {
                                rewardsData.get(j)[i] += arbiter.getReward(j);
                                avgMoveTimeData.get(j)[i] += arbiter.getDecisionTime(j);
                            }
                        }
                        for (int j = 0; count != 0 && j < agents.size(); j += 1) {
                            avgMoveTimeData.get(j)[i] /= count;
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // output
                StringBuilder output = new StringBuilder();

                if (nSimulations == 1) {
                    output.append(arbiter.getHistory());
                }

                // #nSimulations - world
                output.append("#");
                output.append(nSimulations);
                output.append(" - ");
                output.append(domain.getClass().getSimpleName());
                output.append("\n");

                DecimalFormat df = new DecimalFormat("#.###");
                for (int j = 0; j < agents.size(); j++) {
                    output.append(agents.get(j).toString());
                    output.append("\n");
                    double[] rewards = rewardsData.get(j);
                    double[] avgMoveTimes = avgMoveTimeData.get(j);
                    Mean mean = new Mean();
                    StandardDeviation sd = new StandardDeviation();
                    output.append("  reward:         avg = ");
                    output.append(df.format(mean.evaluate(rewards)));
                    output.append(" std = ");
                    output.append(df.format(sd.evaluate(rewards)));
                    output.append("\n");
                    output.append("  move time (ms): avg = ");
                    output.append(df.format(mean.evaluate(avgMoveTimes)));
                    output.append(" std = ");
                    output.append(df.format(sd.evaluate(avgMoveTimes)));
                    output.append("\n");
                }

                if (args.length == 2) {
                    recordResults(args[1], output.toString());
                } else {
                    System.out.println(output.toString());
                }
                arbiter.done();
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Illegal arguments\nusage: java -jar DomainTest.jar test_filename [output_filename]");
        }
    }

    private void readXmlFile(File file) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();

        if (!doc.getDocumentElement().getNodeName().equals(ROOT_ELEMENT)) {
            throw new IllegalArgumentException("Root tag should be '" + ROOT_ELEMENT + "'");
        }
        nSimulations = Integer.parseInt(getTagValue(N_SIMULATIONS_ELEMENT, doc.getDocumentElement()));
        NodeList nList = doc.getElementsByTagName(DOMAIN_ELEMENT);
        Element element = (Element) nList.item(0);
        String simulatorName = getTagValue("name", element);
        NodeList paramList = element.getElementsByTagName("params");
        List<String> simulatorParams = new ArrayList<>();
        if (paramList.getLength() != 0) {
            NodeList paramNodes = ((Element) paramList.item(0)).getElementsByTagName("param");
            for (int j = 0; j < paramNodes.getLength(); j += 1) {
                Node paramNode = paramNodes.item(j);
                if (paramNode.getNodeType() == Node.ELEMENT_NODE) {
                    simulatorParams.add(paramNode.getTextContent());
                }
            }
        }

        domain = Simulators.getSimulator(simulatorName, simulatorParams);

        NodeList agentList = doc.getElementsByTagName("agents");
        NodeList agentNodes = ((Element) agentList.item(0)).getElementsByTagName("agent");
        for (int i = 0; i < agentNodes.getLength(); i += 1) {
            Element agentNode = (Element) agentNodes.item(i);
            if (agentNode.getNodeType() == Node.ELEMENT_NODE) {
                String name = getTagValue("name", agentNode);
                paramList = agentNode.getElementsByTagName("params");
                List<String> params = new ArrayList<>();
                if (paramList.getLength() != 0) {
                    NodeList paramNodes = ((Element) paramList.item(0)).getElementsByTagName("param");
                    for (int j = 0; j < paramNodes.getLength(); j += 1) {
                        Node paramNode = paramNodes.item(j);
                        if (paramNode.getNodeType() == Node.ELEMENT_NODE) {
                            params.add(paramNode.getTextContent());
                        }
                    }
                }
                agents.add(Agents.getAgent(name, params));
            }
        }
    }

    private String getTagValue(String tagName, Element element) {
        return element.getElementsByTagName(tagName).item(0).getChildNodes().item(0).getNodeValue();
    }

    @SuppressWarnings("unchecked")
    private void readYamlFile(File file) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        Map<String, Object> root = (Map<String, Object>) yaml.load(new FileInputStream(file));
        // number of simulations
        nSimulations = (int) root.get(N_SIMULATIONS_ELEMENT);
        // domain
        Map<String, Object> domainSimulator = (Map<String, Object>) root.get(DOMAIN_ELEMENT);
        String domainName = (String)domainSimulator.get("name");
        List<String> domainParams = getYamlStringList((List<Object>) domainSimulator.get("params"));
        domain = Simulators.getSimulator(domainName, domainParams);
        // agents
        List<Map<String, Object>> agentList = (List<Map<String, Object>>)root.get("agents");
        for (Map<String, Object> agentData: agentList) {
            List<String> params = getYamlStringList((List<Object>) agentData.get("params"));
            agents.add(Agents.getAgent((String)agentData.get("name"), params));
        }
    }

    private List<String> getYamlStringList(List<Object> list) {
        List<String> output = new ArrayList<>();
        if (list != null) {
            for (Object item: list) {
                output.add(item.toString());
            }
        }
        return output;
    }

    private void recordResults(String filename, String results) {
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(filename, true));
            output.write(results);
            output.close();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not write to " + filename);
        }
    }

    private void registerSimulators() {
        try {
            Class.forName("com.castlefrog.agl.domains.backgammon.BackgammonSimulatorProvider");
            Class.forName("com.castlefrog.agl.domains.biniax.BiniaxSimulatorProvider");
            Class.forName("com.castlefrog.agl.domains.ewn.EwnSimulatorProvider");
            Class.forName("com.castlefrog.agl.domains.connect4.Connect4SimulatorProvider");
            Class.forName("com.castlefrog.agl.domains.havannah.HavannahSimulatorProvider");
            Class.forName("com.castlefrog.agl.domains.hex.HexSimulatorProvider");
            Class.forName("com.castlefrog.agl.domains.yahtzee.YahtzeeSimulatorProvider");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    private void registerAgents() {
        try {
            Class.forName("com.castlefrog.agl.agents.ConsoleAgentProvider");
            Class.forName("com.castlefrog.agl.agents.RandomAgentProvider");
            Class.forName("com.castlefrog.agl.agents.UctAgentProvider");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        new DomainTest(args);
    }
}
