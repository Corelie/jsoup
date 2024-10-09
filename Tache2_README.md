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

## Coverage après l'ajout de nos tests
|fichier|class| method      | line            | branch          |
|---|---|-------------|-----------------|-----------------|
| general |96% (254/263)| 92% (1733/1879) | 90% (8377/9296) | 86% (8440/9806) |
|Element|100% (3/3)| 100% (153/153) | 99% (498/499)   | 96% (556/576)   |
| Attribute| 100% (1/1)| 88% (23/26) | 92% (92/100)    | 95% (198/208)   |
|Validate|100% (1/1)| 83% (15/18) | 80% (38/47)     | 85% (58/68)     |
| HtmlTreeBuilderState| 96% (26/27)| 98% (70/71) | 89% (1181/1323) | 84% (1450/1720) |

# Explications des tests:

## 1. testCreateElementWithNamespace()
[Lien du test](https://github.com/Corelie/jsoup/blob/b288242cb6c8705215803c34ddb7b612e65b2998/src/test/java/org/jsoup/nodes/ElementTest.java#L1543-L1555)

Ce test a pour but de tester le constructeur suivant de la classe Element:

    public Element(String tag, String namespace) {  
    this(Tag.valueOf(tag, namespace, ParseSettings.preserveCase), null);}  

Ce constructeur a pour but de créer un objet Element dans un namespace spécifique.
Étant donné qu'il s'agit du constructeur de cette classe il est crucial de le tester pour vérifier qu'il fait exactement ce qu'il est supposé faire, c'est à dire créer correctement l'Element voulu. Si cette fonction malfonctionne sans que l'on ne s'en rende compte c'est toute la classe Element et tout le code qui en dépends qui serait sujet à des bugs.

Nous avons donc créé un test simple pour vérifier que ce constructeur fait bien ce qui est attendu de lui, ce test crée un nouvel Element et ensuite vérifie que les valeurs que nous lui avons données lui ont bien été attribuées.

## 2. testGetValidKey()
[Lien du test](https://github.com/Corelie/jsoup/blob/968bf62e0b941c08c0028f06a4a918a097c8750c/src/test/java/org/jsoup/nodes/AttributeTest.java#L103-L151)

Ce test a pour but de tester la méthode getValidKey() de la classe Attribute. Il teste également les deux méthodes appelées par celle-ci : isValidXmlKey et isValidHtmlKey.

La méthode getValidKey() est une méthode très  importante de la classe Attribute puisqu'elle permet de vérifié si la clé d'un attribut html ou xml corresponds à un format valide et la transforme si elle ne l'est pas.

Nous avons donc créé un test paramétré permettant de tester toutes les valeurs limites possibles d'une clé HTML et XML afin de s'assurer que les méthodes fonctionnaient correctement.

Ce processus nous a effectivement permis de trouver une erreur dans le code. La fonction isValidHtmlKey considère le symbole ">" comme pouvant faire partie d'une clé d'attribut html valide.
Or en lisant la [documentation d'html](https://html.spec.whatwg.org/multipage/syntax.html#attributes-2) pour nous assurer que la méthode correspondait bien aux standards, nous avons découvert que le symbole ">" était explicitement mentionné comme ne pouvant pas faire partie d'une clé valide:

> "Attributes have a name and a value. Attribute names must consist of one or more characters **other than** controls, U+0020 SPACE, U+0022 ("), U+0027 ('), **U+003E (>)**, U+002F (/), U+003D (=), and noncharacters."

Cette erreur montre bien l'importance des tests pour cette méthode.

## 3. testAttributeEquals()
[Lien du test](https://github.com/Corelie/jsoup/blob/968bf62e0b941c08c0028f06a4a918a097c8750c/src/test/java/org/jsoup/nodes/AttributeTest.java#L153-L190)

Ce test a pour but de tester la méthode equals() de la classe Attribute. Cette méthode vient définir l'égalité entre deux objets Attribute. Étant donné qu'elle est utilisé à de nombreux endroits dans le code et notamment dans des tests (puisque assertEquals() de deux Attribute vient utiliser cette méthode) il est très important de s'assurer de son bon fonctionnement.
Nous avons utilisé un test paramétré pour tester tous les cas de non égalité ou d'égalité, ainsi que la bibliothèque java-faker pour générer des cas différents.

## 4. testAttributeClone()
[Lien du test](https://github.com/Corelie/jsoup/blob/968bf62e0b941c08c0028f06a4a918a097c8750c/src/test/java/org/jsoup/nodes/AttributeTest.java#L191-L208)

Ce test vérifie la méthode clone() de la classe Attribute.
Cette méthode est utilisée à plusieurs endroits dans le code il est donc important de s'assurer de son bon fonctionnement.
Nous utilisons la bibliothèque java-faker pour génerer un objet Attribute à chaque test, nous appliquons la méthode clone() dessus puis testons pour vérifier qu'il est bien égal à l'original.

## 5. testNotEmpty()
[Lien du test](https://github.com/Corelie/jsoup/blob/968bf62e0b941c08c0028f06a4a918a097c8750c/src/test/java/org/jsoup/helper/ValidateTest.java#L47-L82)

Ce test valide les 2 méthodes notEmpty et la méthode notEmptyParam de la classe Validate. Ces 3 méthodes font exactement la même chose, c'est pourquoi nous avons jugé bon de les réunir dans un même test. Les trois prennent en entrée un string vérifie s'il est vide et si oui lancent une exception. La seule différence est le message affiché:  le premier notEmpty n'en affiche pas, le second prends un string en entrée et l'affiche et notEmptyParam prends le nom du paramètre en entrée et l'affiche dans le message.

Ces méthodes notEmpty vérifient la validité de strings à de nombreux endroits dans le code, il est donc important de vérifier qu'elles fonctionnent correctement sinon ce sont de grosses parties du code qui ne sont pas validées correctement.

Le test vérifie que l'exception est bien lancée et que le bon message est affiché si le string est vide ou null.  Il vérifie également qu'aucune exception n'est levée en cas de string valide.

## 6. testBeforeHeadStateProcess()
[Lien du test](https://github.com/Corelie/jsoup/blob/deb8e4482feea981ee052b7c414a5122cb4d9093/src/test/java/org/jsoup/parser/HtmlTreeBuilderStateTest.java#L129-L169)

Ce test vise à vérifier le comportement de l'état BeforeHead dans le HtmlTreeBuilder lors du traitement de différents types de tokens. Nous testons ici les cas suivants :

    un token de type espace blanc,
    un commentaire,
    une balise de début html et head,
    et une balise de fin inattendue telle que body.

Ces tests sont cruciaux pour s'assurer que le constructeur gère correctement la phase BeforeHead, permettant un traitement fluide des documents HTML bien formés ou mal formés dès le début de la construction du DOM.

## 7. testProcessNobrTagInTreeBuilder()
[Lien du test](https://github.com/Corelie/jsoup/blob/deb8e4482feea981ee052b7c414a5122cb4d9093/src/test/java/org/jsoup/parser/HtmlTreeBuilderStateTest.java#L170-L193)

Ce test vérifie le comportement du HtmlTreeBuilder lors du traitement de la balise nobr dans l'état InBody. La balise nobr est utilisée pour interdire le retour à la ligne dans une section de texte. Le test simule un document HTML contenant les balises html, body, et nobr, et s'assure que la balise est bien gérée et ajoutée aux éléments de formatage actif. Ce test est essentiel pour garantir que le constructeur traite correctement les balises non standards ou spécifiques.


## 8. testInBodyStartApplets()
[Lien du test](https://github.com/Corelie/jsoup/blob/deb8e4482feea981ee052b7c414a5122cb4d9093/src/test/java/org/jsoup/parser/HtmlTreeBuilderStateTest.java#L195-L217)

Ce test examine comment le HtmlTreeBuilder traite la balise applet dans l'état InBody. Cette balise, rarement utilisée, est gérée de manière spéciale dans les documents HTML. Le test simule l'ouverture d'une balise applet, vérifie qu'elle est ajoutée à la pile d'éléments ouverts, puis simule la fermeture de la balise pour s'assurer qu'elle est correctement retirée de la pile. Ce test garantit que la gestion des balises obsolètes comme applet est bien prise en charge par le parser.


## 9. testProcessEndTagCaptionInCaption()
[Lien du test](https://github.com/Corelie/jsoup/blob/deb8e4482feea981ee052b7c414a5122cb4d9093/src/test/java/org/jsoup/parser/HtmlTreeBuilderStateTest.java#L219-L238)

Ce test est conçu pour vérifier le comportement du HtmlTreeBuilder lorsqu'il rencontre une balise de fin caption dans l'état InCaption. La balise caption est utilisée dans les tableaux HTML pour donner un titre aux éléments. Ce test vérifie que la fermeture d'une balise caption dans cet état échoue comme prévu, assurant ainsi une gestion correcte des erreurs et des structures mal formées dans le DOM.


## 10. testProcessEndTagColgroup()
[Lien du test](https://github.com/Corelie/jsoup/blob/deb8e4482feea981ee052b7c414a5122cb4d9093/src/test/java/org/jsoup/parser/HtmlTreeBuilderStateTest.java#L240-L266)

Ce test valide le traitement des balises de fin colgroup dans l'état InColumnGroup. La balise colgroup est utilisée dans les tableaux HTML pour spécifier un groupe de colonnes avec un format particulier. Le test simule un document HTML contenant une balise table et une balise colgroup, puis vérifie que la balise de fin colgroup est traitée correctement, garantissant ainsi que les tableaux HTML sont construits conformément aux spécifications du DOM.
