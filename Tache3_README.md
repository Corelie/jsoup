# Tâche #3
### Mariel Leano -20218008 Corélie Godefroid -20205217
### Choix de l'étude de cas : jsoup

Pour chaque action la section "Flags supplementaires utilises" contient le log du coverage et des flags modifiés pour cette action.
 
# Explications des flags choisis:

## Action 1 Tâche_3:

Cette action utilise les flags -XX:+UseG1GC et -XX:+UseStringDeduplication.

Normalement le flag UseG1GC est déjà activé dans les actions du code (cela est observable dans les log du gc de l'action 2) mais puisque le flag UseStringDeduplication fonctionne avec ce flag, j'ai préféré l'ajouter explicitement afin d'éviter d'éventuels bugs futurs dans un cas où il ne serait plus utilisé par défaut. Il permet une meilleure efficacité du garbage collector, sur des machines à plusieurs processeurs, en libérant des régions presque vides de la heap d'abord ce qui permet de libérer beaucoup d'espace rapidement.

Le flag UseStringDeduplication permet de réunir les strings dupliqués sur la heap en une seule instance ce qui évite de surcharger la mémoire avec des strings identiques. Ce flag est particulièrement utile dans des cas où le code utilise beaucoup de strings ou utilise des strings fréquemment. Étant donné que Jsoup est une librairie qui permet d'extraire, manipuler ou encore vérifier du code HTML, c'est un flag très pertinent à utiliser puisque la manipulation de strings est très fréquente.

Ce flag permet d'améliorer la performance du code en libérant plus d'espace, mais également sa qualité, en s'assurant qu'on a plus d'espace libre dans la mémoire on prévient d'éventuels problèmes de manque de mémoire.

## Action 2 Tâche_3:
Cette action utilise le flag -Xlog:gc*. 

Ce flag permet d'imprimer toutes les actions effectuées par le garbage collector et combien de temps elles durent. C'est une fonctionnalité particulièrement utile afin de surveiller les performances du garbage collector et de la taille de la heap. En effet, en ayant une heap trop petite le garbage collector est obligé de la vider très souvent ce qui emmène un temps de pause au total très long, car les pauses sont trop fréquentes. Au contraire en ayant une heap trop importante les pauses du garbage collector sont moins fréquentes mais très longues. Il faut trouver le bon juste milieu et cela ne peut être fait qu'en observant les logs du garbage collector.

Il est également bon d'observer son fonctionnement afin de pouvoir vérifier que le garbage collector est bien optimisé et qu'aucun changement brusque dans ses performances n'apparaît (ce qui pourrait indiquer un problème autre part).

Personnellement ce flag a pu me servir dans mon travail sur cette tâche, j'essayais initialement d'optimiser la taille de la heap puis la vitesse du garbage collector avant de me rendre compte, grâce aux comparaisons avec cette action (qui avait le flag -Xlog:gc* d'activé), que mes changements n'amélioraient pas vraiment les performances puisque la heap et le garbage collector étaient déjà assez bien optimisé. J'ai donc pu me concentrer sur l'exploration d'autres flags de la JVM à la place.

Ce flag permet donc d'améliorer l'observabilité du code en rendant accessibles les actions du garbage collector.
## Action 3 Tâche_3:
Cette action utilise les flags -XX:+HeapDumpOnOutOfMemoryError, -XX:HeapDumpPath=./java_pid\<pid>_heap_dump.hprof et -XX:+ExitOnOutOfMemoryError .


Ces 3 flags permettent ensemble de correctement gérer une erreur de manque de mémoire. Ce sont des flags utiles à avoir, car des erreurs de mémoire sont des problèmes assez fréquents lors de l'écriture ou de l'utilisation d'un code et peuvent être difficiles à comprendre. 

Le premier flag -XX:+HeapDumpOnOutOfMemoryError permet de vider le contenu de la heap dans un fichier lorsqu'une telle erreur se produit. Cela permet alors de pouvoir vérifier à quel moment l'erreur est survenue grâce aux éléments qui étaient manipulés à ce moment-là, ainsi que de comprendre comment leurs allocations ont rempli la heap et ont causé cette erreur.

Le deuxième flag   -XX:HeapDumpPath=./java_pid\<pid>_heap_dump.hprof permet d'indiquer que l'on veut vider le contenu de la heap dans le fichier ./java_pid\<pid>_heap_dump.hprof . L'inclusion de \<pid> permet au processus d'y insérer l'identifiant du thread ayant causé l'erreur. Avoir cet identifiant permet encore plus de comprendre l'erreur, car on peut alors inspecter exactement l'exécution de ce thread.

Le dernier flag -XX:+ExitOnOutOfMemoryError permet de forcer la fin de l'exécution et évite que l'on poursuivre l'exécution dans un état instable suite à cette erreur.

Ces 3 flags permettent donc d'améliorer la qualité, en s'assurant de ne pas poursuivre l'exécution dans un état instable, et l'observabilité, en rendant accessibles les données de la heap ce qui permet de mieux comprendre le code et d'en diagnostiquer les problèmes.
