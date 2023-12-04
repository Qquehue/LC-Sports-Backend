package school.sptech.lcsports.service;

import br.com.efi.efisdk.EfiPay;
import br.com.efi.efisdk.exceptions.EfiPayException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import school.sptech.lcsports.domain.Usuario;
import school.sptech.lcsports.domain.efi.Assinatura;
import school.sptech.lcsports.efipadrao.Credentials;
import school.sptech.lcsports.repository.AssinaturaRepository;

import java.time.LocalDate;
import java.util.HashMap;

@Service
public class AssinaturaService {
    @Autowired
    AssinaturaRepository assinaturaRepository;

    //ID HOMOLOGACAO
//    String idPlano = "11373";

    //ID PRODUCAO
    String idPlano = "107389";

    public JSONObject obterCredenciais(){
        Credentials credentials = new Credentials();

        JSONObject options = new JSONObject();
        options.put("client_id", credentials.getClientId());
        options.put("client_secret", credentials.getClientSecret());
        options.put("sandbox", credentials.isSandbox());

        return options;
    }

    public String criarAssinatura (Usuario usuario) throws Exception, EfiPayException {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", idPlano);

        // items
        JSONArray items = new JSONArray();

        JSONObject item1 = new JSONObject();
        item1.put("name", "Mensalidade");
        item1.put("amount", 1);
        item1.put("value", 1);

//        JSONObject item2 = new JSONObject("{\"name\":\"Item 2\", \"amount\":1, \"value\":1}");

        items.put(item1);
//        items.put(item2);

        // customer
        JSONObject customer = new JSONObject();
        customer.put("name", usuario.getNome());
        customer.put("cpf", usuario.getCpf());
        customer.put("phone_number", usuario.getTelefone().replaceAll("[^0-9]", ""));

        // notification url
//        JSONObject metadata = new JSONObject();
//        metadata.put("notification_url", "http://domain.com/notification");

        //discount
//        JSONObject discount = new JSONObject();
//        discount.put("type", "currency");
//        discount.put("value", 599);

        //configurations
        JSONObject configurations = new JSONObject();
        configurations.put("fine", 200);
        configurations.put("interest", 33);

        LocalDate validade = LocalDate.now().plusMonths(1);

        JSONObject bankingBillet = new JSONObject();
        bankingBillet.put("expire_at", validade);
        bankingBillet.put("customer", customer);
//        bankingBillet.put("discount", discount);
        bankingBillet.put("configurations", configurations);

        JSONObject payment = new JSONObject();
        payment.put("banking_billet", bankingBillet);

        JSONObject body = new JSONObject();
        body.put("payment", payment);
        body.put("items", items);
//        body.put("metadata", metadata);

        EfiPay efi = null;

        try {

            efi = new EfiPay(obterCredenciais());
            JSONObject response = efi.call("createOneStepSubscription", params, body);
            System.out.println(response);
            if (response.has("data")) {
                JSONObject data = response.getJSONObject("data");
                if (data.has("charge")) {
                    JSONObject charge = data.getJSONObject("charge");
                    if (charge.has("id")) {
                        int idDaCobranca = charge.getInt("id");
                        // Faça o que quiser com o ID, como atribuí-lo a uma variável
                        Assinatura assinatura = new Assinatura(idDaCobranca,usuario.getIdUsuario());
                        assinaturaRepository.save(assinatura);
                    }
                }
            }
            return response.toString();

        } catch (EfiPayException e) {
            System.out.println(e.getCode());
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
            throw e;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }

    }

    public void cancelarAssinatura(Integer idAssinatura){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", String.valueOf(idAssinatura));

        try {
            EfiPay efi = new EfiPay(obterCredenciais());
            JSONObject response = efi.call("cancelSubscription", params, new JSONObject());
            System.out.println(response);
        }catch (EfiPayException e){
            System.out.println(e.getCode());
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public String editarAssinatura() throws Exception, EfiPayException {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", "107389");

        JSONObject body = new JSONObject();
        body.put("notification_url", "http://domain.com/notification");
        body.put("custom_id", "Custom Subscription 0001");

        try {
            EfiPay efi = new EfiPay(obterCredenciais());
            JSONObject response = efi.call("updateSubscriptionMetadata", params, body);
            System.out.println(response);
            return response.toString();
        }catch (EfiPayException e){
            System.out.println(e.getCode());
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
            throw e;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }


    public String criarPlano() throws Exception, EfiPayException{

        JSONObject body = new JSONObject();
        body.put("name", "Plano Premium - LC Sports");
        body.put("interval", 1);
        body.put("repeats", 12);

        try {
            EfiPay efi = new EfiPay(obterCredenciais());
            JSONObject response = efi.call("createPlan", new HashMap<String,String>(), body);
            System.out.println(response);
            return response.toString();
        }catch (EfiPayException e){
            System.out.println(e.getCode());
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
            throw e;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public String listarPlanos() throws EfiPayException, Exception {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", "Plano de Premium - LC Sports");
        params.put("limit", "20");
        params.put("offset", "0");

        try {
            EfiPay efi = new EfiPay(obterCredenciais());
            JSONObject response = efi.call("listPlans", params, new JSONObject());
            System.out.println(response);
            return response.toString();
        }catch (EfiPayException e){
            System.out.println(e.getCode());
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
            throw e;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public String listarAssinaturaPorId(Integer idAssinatura) throws EfiPayException, Exception {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("id", String.valueOf(idAssinatura));
        try {
            EfiPay efi = new EfiPay(obterCredenciais());
            JSONObject response = efi.call("detailSubscription", params, new JSONObject());
            System.out.println(response);
            return response.toString();
        }catch (EfiPayException e){
            System.out.println(e.getCode());
            System.out.println(e.getError());
            System.out.println(e.getErrorDescription());
            throw e;
        }catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
