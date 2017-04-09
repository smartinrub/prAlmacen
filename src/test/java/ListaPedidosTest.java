
import com.sergio.pralmacen.IAgenteDistribuidor;
import com.sergio.pralmacen.ListaPedidos;
import com.sergio.pralmacen.ListaPedidosExcepcion;
import com.sergio.pralmacen.Pedido;
import com.sergio.pralmacen.Pedido.ESTADO;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Tested;
import mockit.Verifications;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.After;

/**
 *
 * @author sergio 
 */
public class ListaPedidosTest {

    private final static int CAPACIDAD = 100;

    @Tested private ListaPedidos lista;

    @Mocked IAgenteDistribuidor agenteDistribuidor;

    @Before
    public void setUp() {
        lista = new ListaPedidos(CAPACIDAD);
    }

    @After
    public void tearDown() {
        lista = null;
    }

    @Test
    public void siSeGeneraUnPedidoEnEsperaElEspacioLibreDeLaListaDisminuyeEnUno() {
        lista.generarPedidoEnEspera();
        assertThat(lista.entradasLibres(), is(equalTo(CAPACIDAD - 1)));
    }

    @Test
    public void siSeGeneraUnPedidoEnEsperaYLaListaEstaVaciaSuIdentificadorEsCero() {
        int id = lista.generarPedidoEnEspera().getId();
        assertThat(id, is(0));
    }

    @Test(expected = ListaPedidosExcepcion.class)
    public void siSeGeneraUnPedidoEnEsperaYLaListaEstaLlenaSeElevaUnaExcepcion() {
        for (int i = 0; i < 100; i++) {
            lista.generarPedidoEnEspera();
        }
        lista.generarPedidoEnEspera(); // pedido 101
    }

    @Test
    public void siSePasaUnPedidoADistribucionYHayAgentesEntoncesElEstadoDelProcesoEsDistribucion() {
        new Expectations() {{
            agenteDistribuidor.hayAgenteDisponible(); result = true;
        }};

        Pedido pedido = lista.generarPedidoEnEspera();
        lista.pasarPedidoADistribucion(pedido.getId(), agenteDistribuidor);

        assertThat(pedido.getEstado(), is(equalTo(ESTADO.DISTRIBUCION)));
    }
    
    @Test
    public void siSePasaUnPedidoADistribucionYNoHayAgentesEntoncesElEstadoDelProcesoEsEnEspera() {
        new Expectations() {{
            agenteDistribuidor.hayAgenteDisponible(); result = false;
        }};

        Pedido pedido = lista.generarPedidoEnEspera();
        lista.pasarPedidoADistribucion(pedido.getId(), agenteDistribuidor);

        assertThat(pedido.getEstado(), is(equalTo(ESTADO.ESPERA)));
    }
    
    @Test
    public void siSePasaUnPedidoADistribucionYSeEliminaEntoncesSeHaLiberadoAlAgenteDistribuidor() {
        new Expectations() {{
            agenteDistribuidor.hayAgenteDisponible(); result = true;
        }};
        
        Pedido pedido = lista.generarPedidoEnEspera();
        lista.pasarPedidoADistribucion(pedido.getId(), agenteDistribuidor);
        lista.eliminaPedido(pedido.getId(), agenteDistribuidor);
        
        new Verifications() {{
            agenteDistribuidor.liberarAgente(); times = 1;
        }};
    }
    
    @Test
    public void siSeEliminaUnPedidoEnEsperaNoSeLiberaNingunAgenteDistribuidor() {
        Pedido pedido = lista.generarPedidoEnEspera();
        lista.eliminaPedido(pedido.getId(), agenteDistribuidor);
        
        new Verifications() {{
           agenteDistribuidor.liberarAgente(); times = 0;
        }};
    }
    
    @Test(expected = ListaPedidosExcepcion.class)
    public void siSeBuscaUnPedidoQueSeHaEliminadoSeElevaUnaExcepcion() {
        Pedido pedido = lista.generarPedidoEnEspera();
        lista.eliminaPedido(pedido.getId(), agenteDistribuidor);
        lista.buscaPedido(pedido.getId());
    }
    
    @Test(expected = ListaPedidosExcepcion.class)
    public void siSeEliminaPedidoYNoExisteSeElevaUnaExcepcion() {
        lista.eliminaPedido(0, agenteDistribuidor);
    }
    
    @Test
    public void siSeEliminaPedidoExistenteEntoncesSuIdentificadorQuedaLibre() {
        Pedido pedido = lista.generarPedidoEnEspera();
        assertThat(lista.identificadorEstaLibre(pedido.getId()), is(false));
        lista.eliminaPedido(pedido.getId(), agenteDistribuidor);
        assertThat(lista.identificadorEstaLibre(pedido.getId()), is(true));
    }
    
    
    @Test
    public void siSePasaUnPedidoADistribucionYHayAgentesEntoncesSeSolicitaAgente() {
        new Expectations() {{
            agenteDistribuidor.hayAgenteDisponible(); result = true;
        }};

        Pedido pedido = lista.generarPedidoEnEspera();
        lista.pasarPedidoADistribucion(pedido.getId(), agenteDistribuidor);
        
        new Verifications() {{
            agenteDistribuidor.solicitarAgente(); times = 1;
        }};
    }

}
