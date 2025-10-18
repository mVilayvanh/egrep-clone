import matplotlib.pyplot as plt

# Nom du fichier contenant les résultats
fichier = "result.txt"

# Lecture du fichier
labels = []
values = []

with open(fichier, "r") as f:
    for ligne in f:
        parts = ligne.strip().split()
        if len(parts) == 2:
            labels.append(parts[0])
            values.append(float(parts[1]))

# Vérification
if not labels:
    print("Erreur : aucun résultat lu dans le fichier.")
    exit(1)

# Création du graphique
plt.figure(figsize=(8, 5))
bars = plt.bar(labels, values)

# Ajouter les valeurs au-dessus des barres
for bar in bars:
    yval = bar.get_height()
    plt.text(bar.get_x() + bar.get_width()/2, yval + 2, f"{yval:.0f} ms", ha='center', va='bottom')

plt.title("Comparaison du temps moyen d’exécution")
plt.xlabel("Programme")
plt.ylabel("Temps moyen (ms)")
plt.grid(axis='y', linestyle='--', alpha=0.6)

# Affichage du graphique
plt.tight_layout()
plt.show()