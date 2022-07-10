package tacos.web;

import ch.qos.logback.core.net.SyslogOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import tacos.Ingredient;
import tacos.Ingredient.Type;
import tacos.Order;
import tacos.Taco;
import tacos.data.IngredientRepository;
import tacos.data.TacoRepository;

import javax.validation.Valid;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("order")
public class DesignTacoController {

    private final IngredientRepository ingredientRepo;

    private TacoRepository tacoRepo;

    @Autowired
    public DesignTacoController(IngredientRepository ingredientRepo,TacoRepository tacoRepo){
        this.ingredientRepo = ingredientRepo;
        this.tacoRepo = tacoRepo;
    }

    @GetMapping
    public String showDesignForm(Model model){
//        List<Ingredient> ingredients = Arrays.asList(
//                new Ingredient("FLTO","Flour Tortilla", Ingredient.Type.WRAP),
//                new Ingredient("COTO","Corn Tortilla", Ingredient.Type.WRAP),
//                new Ingredient("GRBF","Ground Beef", Ingredient.Type.PROTEIN),
//                new Ingredient("CARN","Carnitas", Ingredient.Type.VEGGIES),
//                new Ingredient("TMTO","Diced Tomatoes", Ingredient.Type.VEGGIES),
//                new Ingredient("LETC","Lettuce", Ingredient.Type.CHEESE),
//                new Ingredient("CHED","Cheddar", Ingredient.Type.CHEESE),
//                new Ingredient("JACK","Monterrey Jack", Ingredient.Type.CHEESE),
//                new Ingredient("SLSA","Salsa", Ingredient.Type.SAUCE),
//                new Ingredient("SRCR","Sour Cream", Ingredient.Type.SAUCE)
//        );
        List<Ingredient> ingredients = new ArrayList<>();
        ingredientRepo.findAll().forEach(i -> ingredients.add(i));

        Type[] types = Ingredient.Type.values();
        for (Type type : types){
            model.addAttribute(type.toString().toLowerCase(),filterByType(ingredients,type));
        }
        model.addAttribute("taco",new Taco());

        return "design";
    }

    private List<Ingredient> filterByType(List<Ingredient> ingredients,Type type){
        return ingredients.stream()
                .filter(x->x.getType().equals(type))
                .collect(Collectors.toList());
    }

    @ModelAttribute(name = "order")
    public Order order(){
        return new Order();
    }

    @ModelAttribute(name = "taco")
    public Taco taco(){
        return new Taco();
    }

    @PostMapping
    public String processDesign(@Valid Taco design, Errors errors, @ModelAttribute Order order){
        if (errors.hasErrors()){
            return "design";
        }
//        log.info("Processing design: "+design);

        Taco saved = tacoRepo.save(design);
        order.addDesign(saved);

        return "redirect:/orders/current";
    }
}
