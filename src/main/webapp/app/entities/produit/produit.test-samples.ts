import { IProduit, NewProduit } from './produit.model';

export const sampleWithRequiredData: IProduit = {
  id: 'f264b07e-306c-44d8-bffe-de849700f7c7',
};

export const sampleWithPartialData: IProduit = {
  id: '9e7ed229-3306-4467-9c95-829a2ec260cc',
  nom: 'prout',
};

export const sampleWithFullData: IProduit = {
  id: 'fda21762-44e8-494b-87d9-c2da54108c24',
  description: 'si',
  nom: "glouglou à l'insu de ha ha",
  prix: 22786.1,
};

export const sampleWithNewData: NewProduit = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
