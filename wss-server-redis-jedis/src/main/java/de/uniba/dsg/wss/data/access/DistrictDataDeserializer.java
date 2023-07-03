// package de.uniba.dsg.wss.data.access;
//
// import com.fasterxml.jackson.core.JsonParser;
// import com.fasterxml.jackson.core.JsonProcessingException;
// import com.fasterxml.jackson.core.ObjectCodec;
// import com.fasterxml.jackson.databind.DeserializationContext;
// import com.fasterxml.jackson.databind.JsonNode;
// import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
// import de.uniba.dsg.wss.data.model.AddressData;
// import de.uniba.dsg.wss.data.model.DistrictData;
// import java.io.IOException;
// import java.util.*;
//
//
/// **
// * Implementierung eines Custom-Deserializers für DistrictData, da keine erfolgreiche
// Deserialisierung des DistrictData Objekt aus
// * dem Redis-Server wegen der List<String>customerRefsIds</String> und der
// HashMap<String,String>orderRefsIds</String,String>
// * gelungen ist.
// * Die beiden Datenstrukturen benötigen einen Setter und Getter zur einer  haben jedoch eine
// @InvocationTargetException aufgrund ihrer setterlosen Eingeschaften
// * geworden, obwohl für beide Datenstrukturen ein Sette
// *
// */
//
//
//
// public class DistrictDataDeserializer extends StdDeserializer<DistrictData> {
//  public DistrictDataDeserializer() {
//    this(null);
//  }
//
//  public DistrictDataDeserializer(Class<?> vc) {
//    super(vc);
//  }
//
//  @Override
//  public DistrictData deserialize(
//      JsonParser jsonParser, DeserializationContext deserializationContext)
//      throws IOException, JsonProcessingException {
//    ObjectCodec codec = jsonParser.getCodec();
//    JsonNode node = codec.readTree(jsonParser);
//
//    String id = node.get("id").asText();
//    String warehouseRefId = node.get("warehouseRefId").asText();
//    String name = node.get("name").asText();
//    AddressData address = codec.treeToValue(node.get("address"), AddressData.class);
//    double salesTax = node.get("salesTax").asDouble();
//    double yearToDateBalance = node.get("yearToDateBalance").asDouble();
//
//    DistrictData districtData =
//        new DistrictData(id, warehouseRefId, name, address, salesTax, yearToDateBalance);
//
//    List<String> customerRefsIds = new ArrayList<>();
//    JsonNode customersRefsIdsNode = node.get("customerRefsIds");
//    if (customersRefsIdsNode.isArray()) {
//      for (JsonNode idNode : customersRefsIdsNode) {
//        customerRefsIds.add(idNode.asText());
//      }
//    }
//    districtData.setCustomerRefsIds(customerRefsIds);
//
//    Map<String, String> orderRefsIds = new HashMap<>();
//    JsonNode orderRefsIdsNode = node.get("orderRefsIds");
//    if (orderRefsIdsNode.isObject()) {
//      Iterator<Map.Entry<String, JsonNode>> iterator = orderRefsIdsNode.fields();
//      while (iterator.hasNext()) {
//        Map.Entry<String, JsonNode> entry = iterator.next();
//        orderRefsIds.put(entry.getKey(), entry.getValue().asText());
//      }
//    }
//    districtData.setOrderRefsIds(orderRefsIds);
//
//    return districtData;
//  }
// }
