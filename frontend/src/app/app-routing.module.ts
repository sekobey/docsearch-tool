import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {IndexerComponent} from './indexer/indexer.component';
import {SearchComponent} from './search/search.component';
import {FilesIndexerComponent} from './files-indexer/files-indexer.component';

const routes: Routes = [
  {path: '', redirectTo: '/search', pathMatch: 'full'},
  {path: 'search', component: SearchComponent},
  {path: 'indexer', component: IndexerComponent},
  {path: 'files-indexer', component: FilesIndexerComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
