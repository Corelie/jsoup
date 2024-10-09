# Tâche #2
### Mariel Leano -20218008 Corélie Godefroid -20205217
### Choix de l'étude de cas : jsoup

## Coverage sans l'ajout de nos tests
|fichier|class|method|line| branch          |
|---|---|---|---|-----------------|
| general |96% (254/263)|92% (1730/1879)|89% (8342/9296)| 85% (8360/9806) |
|Element|100% (3/3)|99% (152/153)|99% (496/499)| 96% (556/576)   |
| Attribute| 100% (1/1)|80% (21/26)|87% (87/100)| 75% (158/208)   |
|Validate|100% (1/1)|83% (15/18)| 78% (37/47)| 76% (52/68)     |
| HtmlTreeBuilderState| 96% (26/27)|98% (70/71)|87% (1155/1323)| 82% (1418/1720) |
# Explications des tests:


general: class 96% (254/263) method 92% (1730/1879) line 89% (8342/9296) branch 85% (8360/9806)
element : class 100% (3/3) method 99% (152/153) line 99% (496/499) branch 96% (556/576)
Attribute: class 100% (1/1) method 80% (21/26) line 87% (87/100) branch 75% (158/208)
Validate : class 100% (1/1) method 83% (15/18) line 78% (37/47) branch 76% (52/68)
HtmlTreeBuilderState: class 96% (26/27) method 98% (70/71) line 87% (1155/1323) branch 82% (1418/1720)
## 6. testCreateElementWithNamespace()
[Lien du test](https://github.com/Corelie/jsoup/blob/b288242cb6c8705215803c34ddb7b612e65b2998/src/test/java/org/jsoup/nodes/ElementTest.java#L1543-L1555)

Ce test a pour but de tester le constructeur suivant de la classe Element:

    public Element(String tag, String namespace) {  
    this(Tag.valueOf(tag, namespace, ParseSettings.preserveCase), null);}  

Ce constructeur a pour but de créer un objet Element dans un namespace spécifique.
Étant donné qu'il s'agit du constructeur de cette classe il est crucial de le tester pour vérifier qu'il fait exactement ce qu'il est supposé faire, c'est à dire créer correctement l'Element voulu. Si cette fonction malfonctionne sans que l'on ne s'en rende compte c'est toute la classe Element et tout le code qui en dépends qui serait sujet à des bugs.

Nous avons donc créé un test simple pour vérifier que ce constructeur fait bien ce qui est attendu de lui, ce test crée un nouvel Element et ensuite vérifie que les valeurs que nous lui avons données lui ont bien été attribuées.

## 7. testGetValidKey()
[Lien du test](https://github.com/Corelie/jsoup/blob/968bf62e0b941c08c0028f06a4a918a097c8750c/src/test/java/org/jsoup/nodes/AttributeTest.java#L103-L151)

Ce test a pour but de tester la méthode getValidKey() de la classe Attribute. Il teste également les deux méthodes appelées par celle-ci : isValidXmlKey et isValidHtmlKey.

La méthode getValidKey() est une méthode très  importante de la classe Attribute puisqu'elle permet de vérifié si la clé d'un attribut html ou xml corresponds à un format valide et la transforme si elle ne l'est pas.

Nous avons donc créé un test paramétré permettant de tester toutes les valeurs limites possibles d'une clé HTML et XML afin de s'assurer que les méthodes fonctionnaient correctement.

Ce processus nous a effectivement permis de trouver une erreur dans le code. La fonction isValidHtmlKey considère le symbole ">" comme pouvant faire partie d'une clé d'attribut html valide.
Or en lisant la [documentation d'html](https://html.spec.whatwg.org/multipage/syntax.html#attributes-2) pour nous assurer que la méthode correspondait bien aux standards, nous avons découvert que le symbole ">" était explicitement mentionné comme ne pouvant pas faire partie d'une clé valide:

> "Attributes have a name and a value. Attribute names must consist of one or more characters **other than** controls, U+0020 SPACE, U+0022 ("), U+0027 ('), **U+003E (>)**, U+002F (/), U+003D (=), and noncharacters."

Cette erreur montre bien l'importance des tests pour cette méthode.

## 8. testAttributeEquals()
[Lien du test](https://github.com/Corelie/jsoup/blob/968bf62e0b941c08c0028f06a4a918a097c8750c/src/test/java/org/jsoup/nodes/AttributeTest.java#L153-L190)

Ce test a pour but de tester la méthode equals() de la classe Attribute. Cette méthode vient définir l'égalité entre deux objets Attribute. Étant donné qu'elle est utilisé à de nombreux endroits dans le code et notamment dans des tests (puisque assertEquals() de deux Attribute vient utiliser cette méthode) il est très important de s'assurer de son bon fonctionnement.
Nous avons utilisé un test paramétré pour tester tous les cas de non égalité ou d'égalité, ainsi que la bibliothèque java-faker pour générer des cas différents.

## 9. testAttributeClone()
[Lien du test](https://github.com/Corelie/jsoup/blob/968bf62e0b941c08c0028f06a4a918a097c8750c/src/test/java/org/jsoup/nodes/AttributeTest.java#L191-L208)

Ce test vérifie la méthode clone() de la classe Attribute.
Cette méthode est utilisée à plusieurs endroits dans le code il est donc important de s'assurer de son bon fonctionnement.
Nous utilisons la bibliothèque java-faker pour génerer un objet Attribute à chaque test, nous appliquons la méthode clone() dessus puis testons pour vérifier qu'il est bien égal à l'original.

## 10. testNotEmpty()
[Lien du test](https://github.com/Corelie/jsoup/blob/968bf62e0b941c08c0028f06a4a918a097c8750c/src/test/java/org/jsoup/helper/ValidateTest.java#L47-L82)

Ce test valide les 2 méthodes notEmpty et la méthode notEmptyParam de la classe Validate. Ces 3 méthodes font exactement la même chose, c'est pourquoi nous avons jugé bon de les réunir dans un même test. Les trois prennent en entrée un string vérifie s'il est vide et si oui lancent une exception. La seule différence est le message affiché:  le premier notEmpty n'en affiche pas, le second prends un string en entrée et l'affiche et notEmptyParam prends le nom du paramètre en entrée et l'affiche dans le message.

Ces méthodes notEmpty vérifient la validité de strings à de nombreux endroits dans le code, il est donc important de vérifier qu'elles fonctionnent correctement sinon ce sont de grosses parties du code qui ne sont pas validées correctement.

Le test vérifie que l'exception est bien lancée et que le bon message est affiché si le string est vide ou null.  Il vérifie également qu'aucune exception n'est levée en cas de string valide.
