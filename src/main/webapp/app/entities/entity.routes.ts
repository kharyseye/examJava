import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'produit',
    data: { pageTitle: 'Produits' },
    loadChildren: () => import('./produit/produit.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
