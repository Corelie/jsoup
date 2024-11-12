# Tâche #3
### Mariel Leano -20218008 Corélie Godefroid -20205217
### Choix de l'étude de cas : jsoup

# Explications des flags choisis:

## Action 1 Tâche_3:

Cette action utilise les flags:

## Action 2 Tâche_3:
Cette action utilise le flag -Xlog:gc*. 
Ce flag permet d'imprimer toutes les actions effectuées par le garbage collector et combien de temps elles durent. C'est une fonctionnalité particulièrement utile afin de surveiller les performances du garbage collector et de la taille de la heap. En effet, en ayant une heap trop petite le garbage collector est obligé de la vider très souvent ce qui emmène un temps de pause au total très long, car les pauses sont trop fréquentes. Au contraire en ayant une heap trop importante les pauses du grabage collector sont moins fréquentes mais très longues. Il faut trouver le bon juste milieu et cela ne peut-être fait qu'on observant les log du garbage collector.

Il est également bon d'observer son fonctionement afin de pouvoir vérifier que le garbage collector est bien optimisé et qu'aucun changment brusque dans ses performances n'apparait (ce qui pourrait indiquer un problème autre part).

Personnellement ce flag a pu me servir dans mon travail sur cette tâche, j'essayait initialement d'optimiser la taille de la heap puis la vitesse du garbage collector avant de me rendre compte, grâce aux comparaisons avec cette action (qui avait le flag -Xlog:gc* d'activé), que mes changements n'amélioraient pas vraiment les performance puisque la heap et le garbage collector étaient déjà assez bien optimisé. J'ai donc pu me concentrer sur l'exploration de d'autres flags de la JVM à la place.

Ce flag permet donc d'améliorer l'observabilité du code.
## Action 3 Tâche_3: