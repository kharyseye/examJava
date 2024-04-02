export interface IProduit {
  id: string;
  description?: string | null;
  nom?: string | null;
  prix?: number | null;
}

export type NewProduit = Omit<IProduit, 'id'> & { id: null };
