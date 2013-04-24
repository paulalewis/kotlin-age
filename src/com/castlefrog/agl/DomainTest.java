package com.castlefrog.agl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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

/**
 * This class is used to run tests between agents on simulators.
 */
public class DomainTest {
    public static final String TEST_ROOT_NAME = "domainTest";
    /**
     * @param args
     *      test_filepath - path to the xml test file
     *      output_filepath - optional path to output file
     */
    public DomainTest(String[] args) {
        if (args.length == 1 || args.length == 2) {
            registerSimulators();
            registerAgents();
            try {
                List<Agent> agents = new ArrayList<Agent>();
                File xmlFile = new File(args[0]);
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(xmlFile);
                doc.getDocumentElement().normalize();

                if (!doc.getDocumentElement().getNodeName().equals(TEST_ROOT_NAME))
                    throw new IllegalArgumentException("Root tag should be '" + TEST_ROOT_NAME + "'");
                int nSimulations = Integer.parseInt(getTagValue("nSimulations",doc.getDocumentElement()));
                NodeList nList = doc.getElementsByTagName("world");
                Element element = (Element) nList.item(0);
                String simulatorName = getTagValue("name", element);
                NodeList paramList = element.getElementsByTagName("params");
                String[] simulatorParams = null;
                if (paramList.getLength() != 0) {
                    NodeList paramNodes = ((Element)paramList.item(0)).getElementsByTagName("param");
                    simulatorParams = new String[paramNodes.getLength()];
                    for (int j = 0; j < paramNodes.getLength(); j += 1) {
                        Node paramNode = paramNodes.item(j);
                        if (paramNode.getNodeType() == Node.ELEMENT_NODE) {
                            simulatorParams[j] = paramNode.getTextContent();
                        }
                    }
                }
                Simulator<?, ?> world = Simulators.getSimulator(simulatorName, simulatorParams);

                NodeList agentList = doc.getElementsByTagName("agents");
                NodeList agentNodes = ((Element)agentList.item(0)).getElementsByTagName("agent");
                for (int i = 0; i < agentNodes.getLength(); i += 1) {
                    Element agentNode = (Element)agentNodes.item(i);
                    if (agentNode.getNodeType() == Node.ELEMENT_NODE) {
                        String name = getTagValue("name", (Element) agentNode);
                        paramList = agentNode.getElementsByTagName("params");
                        String[] params = null;
                        if (paramList.getLength() != 0) {
                            NodeList paramNodes = ((Element)paramList.item(0)).getElementsByTagName("param");
                            params = new String[paramNodes.getLength()];
                            for (int j = 0; j < paramNodes.getLength(); j += 1) {
                                Node paramNode = paramNodes.item(j);
                                if (paramNode.getNodeType() == Node.ELEMENT_NODE) {
                                    params[j] = paramNode.getTextContent();
                                }
                            }
                        }
                        agents.add(Agents.getAgent(name, params));
                    }
                }

                List<double[]> rewardsData = new ArrayList<double[]>();
                List<double[]> avgMoveTimeData = new ArrayList<double[]>();
                for (int i = 0; i < agents.size(); i += 1) {
                    rewardsData.add(new double[nSimulations]);
                    avgMoveTimeData.add(new double[nSimulations]);
                }
                Arbiter<?, ?> arbiter = new Arbiter(world.getInitialState(), world, agents);
                for (int i = 0; i < nSimulations; i += 1) {
                    arbiter.reset();
                    try {
                        int count = 0;
                        while (!arbiter.getWorld().isTerminalState()) {
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
                    } catch (InterruptedException e) {}
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
                output.append(world.getClass().getSimpleName());
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
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else
            System.out.println("Illegal arguments\nusage: java -jar DomainTest.jar test_filepath [output_filepath]");
    }

    private String getTagValue(String tagName, Element element) {
        NodeList nList = element.getElementsByTagName(tagName).item(0).getChildNodes();
        return ((Node) nList.item(0)).getNodeValue();
    }

    private void recordResults(String filepath, String results) {
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(filepath, true));
            output.write(results);
            output.close();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not write to " + filepath);
        }
    }

    private void registerSimulators() {
        try {
            Class.forName("com.castlefrog.agl.domains.backgammon.BackgammonSimulatorProvider");
            Class.forName("com.castlefrog.agl.domains.biniax.BiniaxSimulatorProvider");
            Class.forName("com.castlefrog.agl.domains.havannah.HavannahSimulatorProvider");
            Class.forName("com.castlefrog.agl.domains.hex.HexSimulatorProvider");
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
