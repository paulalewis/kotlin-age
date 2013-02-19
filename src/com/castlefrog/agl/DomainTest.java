package com.castlefrog.agl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.castlefrog.agl.Agent;
import com.castlefrog.agl.Agents;
import com.castlefrog.agl.Arbiter;
import com.castlefrog.agl.Simulator;
import com.castlefrog.agl.TurnType;
import com.castlefrog.agl.domains.backgammon.BackgammonSimulator;
import com.castlefrog.agl.domains.biniax.BiniaxSimulator;
import com.castlefrog.agl.domains.havannah.HavannahSimulator;
import com.castlefrog.agl.domains.hex.HexSimulator;
import com.castlefrog.agl.domains.mathax.MathaxSimulator;

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
                NodeList nList = doc.getElementsByTagName("simulator");
                Element element = (Element) nList.item(0);
                String simulatorName = getTagValue("name", element);
                Simulator<?, ?> world = selectSimulator(simulatorName, null);
                Simulator<?, ?> simulatedWorld = selectSimulator(simulatorName, null);

                NodeList agentList = doc.getElementsByTagName("agents");
                NodeList agentNodes = ((Element)agentList.item(0)).getElementsByTagName("agent");
                for (int i = 0; i < agentNodes.getLength(); i += 1) {
                    Node agentNode = agentNodes.item(i);
                    if (agentNode.getNodeType() == Node.ELEMENT_NODE) {
                        String name = getTagValue("name", (Element) agentNode);
                        NodeList paramList = doc.getElementsByTagName("params");
                        NodeList paramNodes = ((Element)paramList.item(0)).getElementsByTagName("param");
                        String[] params = new String[paramNodes.getLength()];
                        for (int j = 0; j < paramNodes.getLength(); j += 1) {
                            Node paramNode = paramNodes.item(j);
                            if (paramNode.getNodeType() == Node.ELEMENT_NODE) {
                                params[j] = paramNode.getTextContent();
                            }
                        }
                        agents.add(Agents.getAgent(name, params));
                    }
                }

                Arbiter<?, ?> arbiter = new Arbiter(world.getInitialState(), world, agents);
                for (int i = 0; i < nSimulations; i += 1) {
                    try {
                        while (!arbiter.getWorld().isTerminalState())
                            arbiter.step();
                    } catch (InterruptedException e) {}
                }

                if (nSimulations == 1)
                    System.out.println(arbiter.getHistory());
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Output format
            // nTrials, avgRewards, stdRewards, avgMoveTime, stdMoveTime,
            // agent, param1, param2, ... paramN
            /*DecimalFormat df = new DecimalFormat("#.###");
            StringBuilder output = new StringBuilder();
            output.append("#");
            output.append(nTrials);
            output.append(" - ");
            output.append(world.getClass().getSimpleName());
            output.append(" - ");
            output.append(simulatedWorld.getClass().getSimpleName());
            output.append("\n");
            String temp;
            int buffer = 10;
            for (int j = 0; j < agents.size(); j++) {
                double[] rewardsData = arbiter.getRewardsData(j);
                double[] avgMoveTimeData = arbiter.getAvgMoveTimeData(j);
                temp = df.format(Statistics.computeMean(rewardsData));
                for (int k = temp.length(); k < buffer; k++)
                    output.append(" ");
                output.append(temp + ",");
                temp = df.format(Statistics
                        .computeStandardDeviation(rewardsData));
                for (int k = temp.length(); k < buffer; k++)
                    output.append(" ");
                output.append(temp + ",");
                temp = df.format(Statistics.computeMean(avgMoveTimeData));
                for (int k = temp.length(); k < buffer; k++)
                    output.append(" ");
                output.append(temp + ",");
                temp = df.format(Statistics
                        .computeStandardDeviation(avgMoveTimeData));
                for (int k = temp.length(); k < buffer; k++)
                    output.append(" ");
                output.append(temp + ",");
                for (int k = 0; k < agentArgs.get(j).length; k++) {
                    for (int l = agentArgs.get(j)[k].length(); l < buffer; l++)
                        output.append(" ");
                    output.append(agentArgs.get(j)[k]);
                    if (k < agentArgs.get(j).length - 1)
                        output.append(",");
                }
                output.append("\n");
            }

            recordResults("domainResults.xml", output.toString());*/

        } else
            System.out.println("Illegal arguments\nusage: java -jar DomainTest.jar test_filepath [output_filepath]");
    }

    private String getTagValue(String tagName, Element element) {
        NodeList nList = element.getElementsByTagName(tagName).item(0).getChildNodes();
        return ((Node) nList.item(0)).getNodeValue();
    }

    private void recordResults(String filepath, String results) {
        //if it doesnt exist create it
        File xmlFile = new File(filepath);
        try {
            BufferedWriter output = new BufferedWriter(new FileWriter(filepath, true));
            output.write(results);
            output.close();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Could not write to " + filepath);
        }
    }

    private Simulator<?, ?> selectSimulator(String name, String[] args) {
        if (name.equalsIgnoreCase("backgammon"))
            return new BackgammonSimulator();
        else if (name.equalsIgnoreCase("biniax"))
            return new BiniaxSimulator();
        else if (name.equalsIgnoreCase("havannah"))
            return new HavannahSimulator(Integer.valueOf(args[0]), TurnType.SEQUENTIAL);
        else if (name.equalsIgnoreCase("hex"))
        	return new HexSimulator(Integer.valueOf(args[0]), TurnType.SEQUENTIAL);
        else if (name.equalsIgnoreCase("mathax"))
        	return new MathaxSimulator();
        else
            throw new IllegalArgumentException("invalid simulator: " + name);
    }

    /**
     * Register agents from the xml filepath.
     */
    private void registerAgents(String filepath) {
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
