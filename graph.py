import matplotlib.pyplot as plt
import numpy as np

fichier = "results.txt"

resultats = {}
ligne_courante = None

with open(fichier, "r") as f:
    for ligne in f:
        ligne = ligne.strip()
        if not ligne:
            continue

        parts = ligne.split(maxsplit=1)

        # Ligne de motif : [fichier] [motif]
        if len(parts) == 2 and parts[0].startswith("ressources/"):
            ligne_courante = ligne
            resultats[ligne_courante] = {}
        elif ligne_courante and len(parts) == 2:
            prog, temps = parts
            try:
                resultats[ligne_courante][prog] = float(temps)
            except ValueError:
                pass

if not resultats:
    print("Erreur : aucun résultat valide trouvé.")
    exit(1)

tests = [t.replace("ressources/", "") for t in resultats.keys()]

programmes = sorted({prog for res in resultats.values() for prog in res})
x = np.arange(len(tests))
largeur = 0.8 / len(programmes)

plt.figure(figsize=(13, 6))

# Tracer les barres
for i, prog in enumerate(programmes):
    valeurs = [resultats[k].get(prog, 0) for k in resultats]
    bars = plt.bar(x + i * largeur, valeurs, width=largeur, label=prog)

    # Ajouter les valeurs au-dessus des barres
    for bar in bars:
        yval = bar.get_height()
        if yval > 0:
            plt.text(bar.get_x() + bar.get_width()/2, yval + max(valeurs)*0.02,
                     f"{yval:.0f}", ha='center', va='bottom', fontsize=8)

plt.xticks(x + largeur * (len(programmes) - 1) / 2, tests, rotation=25, ha='right')
plt.ylabel("Temps moyen (ms)")
plt.title("Comparaison du temps moyen d’exécution selon le fichier et le motif")
plt.grid(axis='y', linestyle='--', alpha=0.6)
plt.legend(title="Programme")
plt.tight_layout()
plt.show()
