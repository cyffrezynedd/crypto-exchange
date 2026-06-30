import { Routes } from '@angular/router'
import { authGuard } from './core/auth.guard'
import { LoginComponent } from './pages/login.component'
import { RegisterComponent } from './pages/register.component'
import { MarketComponent } from './pages/market.component'
import { OrdersComponent } from './pages/orders.component'
import { WalletsComponent } from './pages/wallets.component'
import { FavoritesComponent } from './pages/favorites.component'
import { ShellComponent } from './layout/shell.component'

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    children: [
      { path: '', component: FavoritesComponent },
      { path: 'market', component: MarketComponent },
      { path: 'orders', component: OrdersComponent },
      { path: 'wallets', component: WalletsComponent },
    ],
  },
  { path: '**', redirectTo: '' },
]
