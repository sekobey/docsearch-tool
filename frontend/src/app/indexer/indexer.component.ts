import {Component, OnInit} from '@angular/core';
import {IndexableItem} from '../indexable.item';
import {IndexerService} from '../indexer.service';

@Component({
  selector: 'app-indexer',
  templateUrl: './indexer.component.html',
  styleUrls: ['./indexer.component.css']
})
export class IndexerComponent implements OnInit {

  constructor(private indexerService: IndexerService) {
  }

  index(rows: string): void {
    const row = rows.split('\n');
    const indexableItems = row.map(this.getIndexableItem);
    this.indexerService.index(indexableItems);
  }

  private getIndexableItem(row: string): IndexableItem {
    const items = row.split(',');
    const indexItem: IndexableItem = {version: `${items[0]}`, fileName: `${items[1]}`, url: `${items[2]}`};
    return indexItem;
  }

  ngOnInit() {
  }

}
